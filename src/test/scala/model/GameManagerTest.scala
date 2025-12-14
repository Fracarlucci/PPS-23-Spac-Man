package model

import org.scalatest.flatspec.AnyFlatSpec
import model.map.GameMapFactory
import org.scalatest.matchers.should.Matchers
import model.MapDSL
import model.Position2D
import model.GhostBasic
import model.Direction
import model.GameEntity
import model.DotBasic
import model.DotPower
import model.board
import model.Wall
import model.Tunnel

class GameManagerTest extends AnyFlatSpec with Matchers:
    def createBasicTestSetup(): SimpleGameManager =
        val spacMan   = SpacManWithLife(Position2D(1, 1), Direction.Right, 0, 1)
        val ghost1    = GhostBasic(Position2D(1, 2), Direction.Right, 1)
        val ghost2    = GhostBasic(Position2D(4, 2), Direction.Left, 2)
        val dotBasic1 = DotBasic(Position2D(2, 1))
        val dotBasic2 = DotBasic(Position2D(3, 2))
        val dotPower1 = DotPower(Position2D(2, 2))
        val fruit     = DotFruit(Position2D(3, 1))
        val tunnel1   = Tunnel(Position2D(2, 0), Position2D(1, 9), Direction.Up)
        val tunnel2   = Tunnel(Position2D(1, 9), Position2D(2, 0), Direction.Down)
        val wall      = Wall(Position2D(0, 1))
        val map       = board(10, 10)
        val dsl       = MapDSL(map)

        import dsl.*

        place the spacMan
        place the ghost1
        place the ghost2
        place the dotBasic1
        place the dotBasic2
        place the dotPower1
        place the fruit
        place the tunnel1
        place the tunnel2
        place the wall

        SimpleGameManager(spacMan, dsl.map)

    def createEdgeTestSetup(): SimpleGameManager =
        val spacMan = SpacManWithLife(Position2D(1, 1), Direction.Right, 0, 1)
        val ghost1  = GhostBasic(Position2D(2, 2), Direction.Right, 1)
        val wall1   = Wall(Position2D(1, 2))
        val wall2   = Wall(Position2D(2, 1))
        val wall3   = Wall(Position2D(3, 2))
        val wall4   = Wall(Position2D(2, 3))
        val map     = board(10, 10)
        val dsl     = MapDSL(map)

        import dsl.*

        place the spacMan
        place the ghost1
        place the wall1
        place the wall2
        place the wall3
        place the wall4

        SimpleGameManager(spacMan, dsl.map)

    def createEdgeTestSetup2(): SimpleGameManager =
        val spacMan = SpacManWithLife(Position2D(2, 3), Direction.Right, 0, 1)
        val ghost1  = GhostBasic(Position2D(2, 2), Direction.Right, 1)
        val wall1   = Wall(Position2D(1, 2))
        val wall2   = Wall(Position2D(2, 1))
        val wall3   = Wall(Position2D(3, 2))
        val map     = board(10, 10)
        val dsl     = MapDSL(map)

        import dsl.*

        place the spacMan
        place the ghost1
        place the wall1
        place the wall2
        place the wall3

        SimpleGameManager(spacMan, dsl.map)

    def createEdgeTestSetup3(): SimpleGameManager =
        val spacMan = SpacManWithLife(Position2D(2, 1), Direction.Right, 0, 3)
        val ghost1  = GhostBasic(Position2D(2, 2), Direction.Right, 1)
        val wall1   = Wall(Position2D(1, 2))
        val wall2   = Wall(Position2D(2, 1))
        val wall3   = Wall(Position2D(3, 2))
        val map     = board(10, 10, Position2D(9, 9))
        val dsl     = MapDSL(map)

        import dsl.*

        place the spacMan
        place the ghost1
        place the wall1
        place the wall2
        place the wall3

        SimpleGameManager(spacMan, dsl.map)

    it should "create a GameManager" in:
        val spacMan     = SpacManWithLife(Position2D(1, 1), Direction.Right, 0, 1)
        val map         = board(10, 10)
        val gameManager = SimpleGameManager(spacMan, map)
        assert(
          gameManager.getSpacMan == spacMan &&
              gameManager.getGameMap == map &&
              gameManager.isGameOver() == false
        )

    it should "move SpacMan" in:
        val gameManager     = createBasicTestSetup()
        gameManager.moveSpacMan(Direction.Up)
        
        
        assert(gameManager.getSpacMan.position == Position2D(1, 0))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 1)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.entityAt(Position2D(1, 0)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))

    it should "move SpacMan multiple times" in:
        val gameManager     = createBasicTestSetup()
        gameManager.moveSpacMan(Direction.Up)
        
        
        assert(gameManager.getSpacMan.position == Position2D(1, 0))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 1)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.entityAt(Position2D(1, 0)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))

        gameManager.moveSpacMan(Direction.Right)
        
        
        assert(gameManager.getSpacMan.position == Position2D(2, 0))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 0)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.entityAt(Position2D(2, 0)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))

    it should "not move SpacMan if blocked by a wall" in:
        val gameManager     = createBasicTestSetup()
        gameManager.moveSpacMan(Direction.Left)
        
        
        assert(gameManager.getSpacMan.position == Position2D(1, 1))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 1)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))

    it should "not move SpacMan outside map boundaries" in:
        val gameManager     = createBasicTestSetup()
        gameManager.moveSpacMan(Direction.Up)
        
        
        assert(gameManager.getSpacMan.position == Position2D(1, 0))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 0)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))

        gameManager.moveSpacMan(Direction.Up)
        
        
        assert(gameManager.getSpacMan.position == Position2D(1, 0))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 0)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))

    it should "move Ghosts" in:
        val gameManager = createBasicTestSetup()
        val movedGhosts = gameManager.moveGhosts()
        assert(gameManager.getGameMap.getGhosts.head.position != Position2D(1, 2))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 2)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.getGhosts.head.position != Position2D(4, 2))
        assert(gameManager.getGameMap.entityAt(Position2D(4, 2)).getOrElse(Set()).isEmpty)

    it should "not move Ghosts if it is blocked" in:
        val gameManager = createEdgeTestSetup()
        gameManager.moveGhosts()
        assert(gameManager.getGameMap.getGhosts.exists(_.position == Position2D(2, 2)))
        assert(gameManager.getGameMap.entityAt(Position2D(2, 2)).getOrElse(Set()).exists(
          _.isInstanceOf[GhostBasic]
        ))

    it should "eat basicDot and increase score" in:
        val gameManager     = createBasicTestSetup()
        gameManager.moveSpacMan(Direction.Right)
        
        
        assert(gameManager.getSpacMan.position == Position2D(2, 1))
        assert(gameManager.getGameMap.entityAt(Position2D(2, 1)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))
        assert(gameManager.getSpacMan.score == DOT_BASIC_SCORE)

    it should "eat powerDot and increase score" in:
        val gameManager = createBasicTestSetup()

        gameManager.moveSpacMan(Direction.Right)
        
        assert(gameManager.getSpacMan.position == Position2D(2, 1))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 1)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.entityAt(Position2D(2, 1)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))
        assert(gameManager.getSpacMan.score == DOT_BASIC_SCORE)

        gameManager.moveSpacMan(Direction.Down)
        assert(gameManager.getSpacMan.position == Position2D(2, 2))
        assert(gameManager.getGameMap.entityAt(Position2D(2, 1)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.entityAt(Position2D(2, 2)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))
        assert(gameManager.getSpacMan.score == DOT_BASIC_SCORE + DOT_POWER_SCORE)

    it should "eat dots, fruit, increase score and add life" in:
        val gameManager     = createBasicTestSetup()
        gameManager.moveSpacMan(Direction.Right)
        
        
        assert(gameManager.getSpacMan.position == Position2D(2, 1))
        assert(gameManager.getSpacMan.score == DOT_BASIC_SCORE)
        gameManager.moveSpacMan(Direction.Right)
        
        assert(gameManager.getSpacMan.position == Position2D(3, 1))
        assert(gameManager.getSpacMan.lives == 2) // Because for this test Spacman has 1 life on default
        assert(gameManager.getSpacMan.score == DOT_BASIC_SCORE + DOT_FRUIT_SCORE)

    it should "detect win condition" in:
        val gameManager = createBasicTestSetup()
        assert(!gameManager.isWin())

        gameManager.moveSpacMan(Direction.Right)
        
        assert(gameManager.getSpacMan.position == Position2D(2, 1))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 1)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.entityAt(Position2D(2, 1)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))
        assert(!gameManager.isWin())

        gameManager.moveSpacMan(Direction.Down)
        assert(gameManager.getSpacMan.position == Position2D(2, 2))
        assert(gameManager.getGameMap.entityAt(Position2D(2, 1)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.entityAt(Position2D(2, 2)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))

        assert(!gameManager.isWin())

        gameManager.moveSpacMan(Direction.Right)
        assert(gameManager.getSpacMan.position == Position2D(3, 2))
        assert(gameManager.getGameMap.entityAt(Position2D(2, 2)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.entityAt(Position2D(3, 2)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))

        gameManager.moveSpacMan(Direction.Up)
        assert(gameManager.getSpacMan.position == Position2D(3, 1))
        assert(gameManager.getGameMap.entityAt(Position2D(3, 2)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.entityAt(Position2D(3, 1)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))
        assert(gameManager.getSpacMan.score == 2 * DOT_BASIC_SCORE + DOT_POWER_SCORE + DOT_FRUIT_SCORE)
        assert(gameManager.isWin())

    it should "set game over when colliding with a ghost" in:
        val gameManager     = createBasicTestSetup()
        gameManager.moveSpacMan(Direction.Down)
        assert(gameManager.getGameMap.entityAt(Position2D(1, 1)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.entityAt(Position2D(1, 2)).getOrElse(Set()).exists(
          !_.isInstanceOf[SpacManWithLife]
        ))
        assert(gameManager.getSpacMan.lives == 0)
        assert(gameManager.isGameOver())

    it should "teleport SpacMan through tunnel with correct direction" in:
        val gameManager = createBasicTestSetup()
        gameManager.moveSpacMan(Direction.Right)
        gameManager.moveSpacMan(Direction.Up)
        
        
        assert(gameManager.getSpacMan.position == Position2D(1, 9) && gameManager.getSpacMan.direction == Direction.Up)
        assert(gameManager.getGameMap.entityAt(Position2D(1, 9)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))

    it should "not teleport SpacMan through tunnel with wrong direction" in:
        val gameManager = createBasicTestSetup()

        gameManager.moveSpacMan(Direction.Up)
        gameManager.moveSpacMan(Direction.Right)

        
        
        assert(gameManager.getSpacMan.position == Position2D(
          2,
          0
        ) && gameManager.getSpacMan.direction == Direction.Right)
        assert(gameManager.getGameMap.entityAt(Position2D(2, 0)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))

    it should "set game over when ghost moves into SpacMan position" in:
        val gameManager = createEdgeTestSetup2()

        assert(!gameManager.isGameOver())
        var attempts = 0
        while !gameManager.isGameOver() && attempts < 10 do
            gameManager.moveGhosts()
            attempts += 1

        assert(gameManager.isGameOver())

    it should "lose a life and teleport Spacman in spawnPoint" in:
        val gameManager = createEdgeTestSetup3()
        assert(!gameManager.isGameOver())

        gameManager.moveSpacMan(Direction.Down)

        assert(gameManager.getSpacMan.lives == 2)
        assert(gameManager.getSpacMan.position == Position2D(9, 9))
        assert(!gameManager.isGameOver())

    it should "eat dotPower, eat ghost and finish chase mode" in:
        val gameManager       = createBasicTestSetup()
        val chaseTimeDuration = 10000

        assert(!gameManager.isChaseMode)

        assert(gameManager.getGameMap.entityAt(Position2D(1, 1)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))

        gameManager.moveSpacMan(Direction.Right)
        assert(gameManager.getGameMap.entityAt(Position2D(2, 1)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))
        assert(gameManager.getSpacMan.score == DOT_BASIC_SCORE)

        gameManager.moveSpacMan(Direction.Down)
        assert(gameManager.getGameMap.entityAt(Position2D(2, 2)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))
        assert(gameManager.getSpacMan.score == DOT_BASIC_SCORE + DOT_POWER_SCORE)

        gameManager.moveSpacMan(Direction.Left)
        assert(gameManager.getGameMap.entityAt(Position2D(1, 2)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))
        assert(
          gameManager.getSpacMan.score == DOT_BASIC_SCORE + DOT_POWER_SCORE + GHOST_BASIC_SCORE
        )

        assert(gameManager.getGameMap.ghostSpawnPoints.exists(spawnPos =>
            gameManager.getGameMap.entityAt(spawnPos).getOrElse(Set()).exists(
              _.isInstanceOf[GhostBasic]
            )
        ))

        assert(gameManager.isChaseMode)

        // Finish chase mode
        gameManager.updateChaseTime(chaseTimeDuration)

        assert(!gameManager.isChaseMode)

        assert(!gameManager.isGameOver())
