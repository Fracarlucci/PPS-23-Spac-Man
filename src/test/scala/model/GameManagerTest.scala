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
import model.board
import model.Wall
import model.Tunnel

class GameManagerTest extends AnyFlatSpec with Matchers:
    def createBasicTestSetup(): SimpleGameManager =
        val spacMan = SpacManWithLife(Position2D(1, 1), Direction.Right, 0, 1)
        val ghost1  = GhostBasic(Position2D(1, 2), Direction.Right, 1.0, 1)
        val ghost2  = GhostBasic(Position2D(4, 2), Direction.Left, 1.0, 2)
        val dot1    = DotBasic(Position2D(2, 1))
        val fruit    = DotFruit(Position2D(3, 1))
        val tunnel1 = Tunnel(Position2D(2, 0), Position2D(1, 9), Direction.Up)
        val tunnel2 = Tunnel(Position2D(1, 9), Position2D(2, 0), Direction.Down)
        val wall    = Wall(Position2D(0, 1))
        val map     = board(10, 10)
        val dsl     = MapDSL(map)

        import dsl.*

        place a spacMan at position(1, 1)
        place a ghost1 at position(1, 2)
        place a ghost2 at position(4, 2)
        place a dot1 at position(2, 1)
        place a fruit at position(3, 1)
        place a tunnel1 at position(2, 0)
        place a tunnel2 at position(1, 9)
        place a wall at position(0, 1)

        SimpleGameManager(spacMan, dsl.map)

    def createEdgeTestSetup(): SimpleGameManager =
        val spacMan = SpacManWithLife(Position2D(1, 1), Direction.Right, 0, 1)
        val ghost1  = GhostBasic(Position2D(2, 2), Direction.Right, 1.0, 1)
        val wall1   = Wall(Position2D(1, 2))
        val wall2   = Wall(Position2D(2, 1))
        val wall3   = Wall(Position2D(3, 2))
        val wall4   = Wall(Position2D(2, 3))
        val map     = board(10, 10)
        val dsl     = MapDSL(map)

        import dsl.*

        place a spacMan at position(1, 1)
        place a ghost1 at position(2, 2)
        place a wall1 at position(1, 2)
        place a wall2 at position(2, 1)
        place a wall3 at position(3, 2)
        place a wall4 at position(2, 3)

        SimpleGameManager(spacMan, dsl.map)

    def createEdgeTestSetup2(): SimpleGameManager =
        val spacMan = SpacManWithLife(Position2D(2, 3), Direction.Right, 0, 1)
        val ghost1  = GhostBasic(Position2D(2, 2), Direction.Right, 1.0, 1)
        val wall1   = Wall(Position2D(1, 2))
        val wall2   = Wall(Position2D(2, 1))
        val wall3   = Wall(Position2D(3, 2))
        val map     = board(10, 10)
        val dsl     = MapDSL(map)

        import dsl.*

        place a spacMan at position(2, 3)
        place a ghost1 at position(2, 2)
        place a wall1 at position(1, 2)
        place a wall2 at position(2, 1)
        place a wall3 at position(3, 2)

        SimpleGameManager(spacMan, dsl.map)

    def createEdgeTestSetup3(): SimpleGameManager =
        val spacMan = SpacManWithLife(Position2D(2, 1), Direction.Right, 0, 3)
        val ghost1  = GhostBasic(Position2D(2, 2), Direction.Right, 1.0, 1)
        val wall1   = Wall(Position2D(1, 2))
        val wall2   = Wall(Position2D(2, 1))
        val wall3   = Wall(Position2D(3, 2))
        val map     = board(10, 10, Position2D(9, 9))
        val dsl     = MapDSL(map)

        import dsl.*

        place a spacMan at position(2, 1)
        place a ghost1 at position(2, 2)
        place a wall1 at position(1, 2)
        place a wall2 at position(2, 1)
        place a wall3 at position(3, 2)

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
        val movedSpacManOpt = gameManager.moveSpacManAndCheck(Direction.Up)
        assert(movedSpacManOpt.isDefined)
        val movedSpacMan = movedSpacManOpt.get
        assert(movedSpacMan.position == Position2D(1, 0))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 1)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.entityAt(Position2D(1, 0)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))

    it should "move SpacMan multiple times" in:
        val gameManager     = createBasicTestSetup()
        var movedSpacManOpt = gameManager.moveSpacManAndCheck(Direction.Up)
        assert(movedSpacManOpt.isDefined)
        var movedSpacMan = movedSpacManOpt.get
        assert(movedSpacMan.position == Position2D(1, 0))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 1)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.entityAt(Position2D(1, 0)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))

        movedSpacManOpt = gameManager.moveSpacManAndCheck(Direction.Right)
        assert(movedSpacManOpt.isDefined)
        movedSpacMan = movedSpacManOpt.get
        assert(movedSpacMan.position == Position2D(2, 0))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 0)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.entityAt(Position2D(2, 0)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))

    it should "not move SpacMan if blocked by a wall" in:
        val gameManager     = createBasicTestSetup()
        val movedSpacManOpt = gameManager.moveSpacManAndCheck(Direction.Left)
        assert(movedSpacManOpt.isDefined)
        val movedSpacMan = movedSpacManOpt.get
        assert(movedSpacMan.position == Position2D(1, 1))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 1)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))

    it should "not move SpacMan outside map boundaries" in:
        val gameManager     = createBasicTestSetup()
        var movedSpacManOpt = gameManager.moveSpacManAndCheck(Direction.Up)
        assert(movedSpacManOpt.isDefined)
        var movedSpacMan = movedSpacManOpt.get
        assert(movedSpacMan.position == Position2D(1, 0))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 0)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))

        movedSpacManOpt = gameManager.moveSpacManAndCheck(Direction.Up)
        assert(movedSpacManOpt.isDefined)
        movedSpacMan = movedSpacManOpt.get
        assert(movedSpacMan.position == Position2D(1, 0))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 0)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))

    it should "move Ghosts" in:
        val gameManager = createBasicTestSetup()
        val movedGhosts = gameManager.moveGhosts()
        assert(movedGhosts.head.position != Position2D(1, 2))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 2)).getOrElse(Set()).isEmpty)
        assert(movedGhosts.head.position != Position2D(4, 2))
        assert(gameManager.getGameMap.entityAt(Position2D(4, 2)).getOrElse(Set()).isEmpty)

    it should "not move Ghosts if it is blocked" in:
        val gameManager = createEdgeTestSetup()

        val movedGhosts = gameManager.moveGhosts()
        assert(movedGhosts.exists(_.position == Position2D(2, 2)))
        assert(gameManager.getGameMap.entityAt(Position2D(2, 2)).getOrElse(Set()).exists(
          _.isInstanceOf[GhostBasic]
        ))

    it should "eat dot and increase score" in:
        val gameManager     = createBasicTestSetup()
        val movedSpacManOpt = gameManager.moveSpacManAndCheck(Direction.Right)
        assert(movedSpacManOpt.isDefined)
        val movedSpacMan = movedSpacManOpt.get
        assert(movedSpacMan.position == Position2D(2, 1))
        assert(gameManager.getGameMap.entityAt(Position2D(2, 1)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))
        assert(movedSpacMan.score == DOT_BASIC_SCORE)

    it should "eat dot + fruit, increase score + add life" in:
        val gameManager     = createBasicTestSetup()
        var movedSpacManOpt = gameManager.moveSpacManAndCheck(Direction.Right)
        assert(movedSpacManOpt.isDefined)
        var movedSpacMan = movedSpacManOpt.get
        assert(movedSpacMan.position == Position2D(2, 1))
        assert(movedSpacMan.score == DOT_BASIC_SCORE)
        movedSpacManOpt = gameManager.moveSpacManAndCheck(Direction.Right)
        movedSpacMan = movedSpacManOpt.get
        assert(movedSpacMan.position == Position2D(3, 1))
        assert(movedSpacMan.lives == 2) // Because for this test Spacman has 1 life on default
        assert(movedSpacMan.score == DOT_BASIC_SCORE + DOT_FRUIT_SCORE)

    it should "detect win condition" in:
        val gameManager = createBasicTestSetup()
        assert(!gameManager.isWin())

        val movedSpacManOpt = gameManager.moveSpacManAndCheck(Direction.Right)
        assert(movedSpacManOpt.isDefined)
        val movedSpacMan = movedSpacManOpt.get
        assert(movedSpacMan.position == Position2D(2, 1))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 1)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.entityAt(Position2D(2, 1)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))
        assert(movedSpacMan.score == DOT_BASIC_SCORE)
        assert(!gameManager.isWin())

        val movedSpacManOpt2 = gameManager.moveSpacManAndCheck(Direction.Right)
        assert(movedSpacManOpt2.isDefined)
        val updatedSpacMan = movedSpacManOpt2.get
        assert(updatedSpacMan.position == Position2D(3, 1))
        assert(gameManager.getGameMap.entityAt(Position2D(2, 1)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.entityAt(Position2D(3, 1)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))
        assert(updatedSpacMan.score == DOT_BASIC_SCORE + DOT_FRUIT_SCORE)
        assert(gameManager.isWin())

    it should "set game over when colliding with a ghost" in:
        val gameManager     = createBasicTestSetup()
        val movedSpacManOpt = gameManager.moveSpacManAndCheck(Direction.Down)
        assert(gameManager.getGameMap.entityAt(Position2D(1, 1)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.entityAt(Position2D(1, 2)).getOrElse(Set()).exists(
          !_.isInstanceOf[SpacManWithLife]
        ))
        assert(!movedSpacManOpt.isDefined)
        assert(gameManager.isGameOver())

    it should "teleport SpacMan through tunnel with correct direction" in:
        val gameManager = createBasicTestSetup()
        gameManager.moveSpacManAndCheck(Direction.Right)
        val movedSpacManOpt = gameManager.moveSpacManAndCheck(Direction.Up)
        assert(movedSpacManOpt.isDefined)
        val movedSpacMan = movedSpacManOpt.get
        assert(movedSpacMan.position == Position2D(1, 9) && movedSpacMan.direction == Direction.Up)
        assert(gameManager.getGameMap.entityAt(Position2D(1, 9)).getOrElse(Set()).exists(
          _.isInstanceOf[SpacManWithLife]
        ))

    it should "not teleport SpacMan through tunnel with wrong direction" in:
        val gameManager = createBasicTestSetup()

        gameManager.moveSpacManAndCheck(Direction.Up)
        val movedSpacManOpt = gameManager.moveSpacManAndCheck(Direction.Right)

        assert(movedSpacManOpt.isDefined)
        val movedSpacMan = movedSpacManOpt.get
        assert(movedSpacMan.position == Position2D(
          2,
          0
        ) && movedSpacMan.direction == Direction.Right)
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

        gameManager.moveSpacManAndCheck(Direction.Down)

        assert(gameManager.getSpacMan.lives == 2)
        assert(gameManager.getSpacMan.position == Position2D(9, 9))
        assert(!gameManager.isGameOver())
