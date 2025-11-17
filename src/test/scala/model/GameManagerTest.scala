package model

import org.scalatest.flatspec.AnyFlatSpec
import model.map.GameMapFactory
import org.scalatest.matchers.should.Matchers
import model.MapDSL
import model.Position2D
import model.SpacManBasic
import model.GhostBasic
import model.Direction
import model.GameEntity
import model.DotBasic
import model.board
import model.Wall

class GameManagerTest extends AnyFlatSpec with Matchers:
    def createTestSetup(): SimpleGameManager =
        val spacMan = SpacManBasic(Position2D(1, 1), Direction.Right, 0)
        val ghost1 = GhostBasic(Position2D(1, 2), Direction.Right, 1.0, 1)
        val ghost2 = GhostBasic(Position2D(4, 2), Direction.Left, 1.0, 2)
        val dot1 = DotBasic(Position2D(2, 1))
        val dot2 = DotBasic(Position2D(3, 1))
        val wall = Wall(Position2D(0, 1))
        val map = board(10, 10)
        val dsl = MapDSL(map)

        import dsl.*

        place a spacMan at position(1, 1)
        place a ghost1 at position(1, 2)
        place a ghost2 at position(4, 2)
        place a dot1 at position(2, 1)
        place a dot2 at position(3, 1)
        place a wall at position(0, 1)

        SimpleGameManager(spacMan, dsl.map)

    it should "create a GameManager" in:
        val spacMan = SpacManBasic(Position2D(1, 1), Direction.Right, 0)
        val map = board(10, 10)
        val gameManager = SimpleGameManager(spacMan, map)
        assert(gameManager.getSpacMan == spacMan && gameManager.getGameMap == map && gameManager.isGameOver() == false)

    it should "move SpacMan" in:
        val gameManager = createTestSetup()
        val movedSpacManOpt = gameManager.moveSpacManAndCheck(Direction.Up)
        assert(movedSpacManOpt.isDefined)
        val movedSpacMan = movedSpacManOpt.get
        assert(movedSpacMan.position == Position2D(1, 0))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 1)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.entityAt(Position2D(1, 0)).getOrElse(Set()).exists(_.isInstanceOf[SpacManBasic]))

    it should "move SpacMan multiple times" in:
        val gameManager = createTestSetup()
        var movedSpacManOpt = gameManager.moveSpacManAndCheck(Direction.Up)
        assert(movedSpacManOpt.isDefined)
        var movedSpacMan = movedSpacManOpt.get
        assert(movedSpacMan.position == Position2D(1, 0))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 1)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.entityAt(Position2D(1, 0)).getOrElse(Set()).exists(_.isInstanceOf[SpacManBasic]))

        movedSpacManOpt = gameManager.moveSpacManAndCheck(Direction.Right)
        assert(movedSpacManOpt.isDefined)
        movedSpacMan = movedSpacManOpt.get
        assert(movedSpacMan.position == Position2D(2, 0))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 0)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.entityAt(Position2D(2, 0)).getOrElse(Set()).exists(_.isInstanceOf[SpacManBasic]))
    
    it should "not move SpacMan if blocked by a wall" in:
        val gameManager = createTestSetup()
        val movedSpacManOpt = gameManager.moveSpacManAndCheck(Direction.Left)
        assert(movedSpacManOpt.isDefined)
        val movedSpacMan = movedSpacManOpt.get
        assert(movedSpacMan.position == Position2D(1, 1))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 1)).getOrElse(Set()).exists(_.isInstanceOf[SpacManBasic]))

    it should "not move SpacMan outside map boundaries" in:
        val gameManager = createTestSetup()
        var movedSpacManOpt = gameManager.moveSpacManAndCheck(Direction.Up)
        assert(movedSpacManOpt.isDefined)
        var movedSpacMan = movedSpacManOpt.get
        assert(movedSpacMan.position == Position2D(1, 0))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 0)).getOrElse(Set()).exists(_.isInstanceOf[SpacManBasic]))

        movedSpacManOpt = gameManager.moveSpacManAndCheck(Direction.Up)
        assert(movedSpacManOpt.isDefined)
        movedSpacMan = movedSpacManOpt.get
        assert(movedSpacMan.position == Position2D(1, 0))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 0)).getOrElse(Set()).exists(_.isInstanceOf[SpacManBasic]))
    
    it should "move Ghosts" in:
        val gameManager = createTestSetup()
        val movedGhosts = gameManager.moveGhosts()
        assert(movedGhosts.head.position != Position2D(1, 2))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 2)).getOrElse(Set()).isEmpty)
        assert(movedGhosts.head.position != Position2D(4, 2))
        assert(gameManager.getGameMap.entityAt(Position2D(2, 2)).getOrElse(Set()).isEmpty)

    it should "not move Ghosts if it is blocked" in:
        val spacMan = SpacManBasic(Position2D(1, 1), Direction.Right, 0)
        val ghost1 = GhostBasic(Position2D(2, 2), Direction.Right, 1.0, 1)
        val wall1 = Wall(Position2D(1, 2))
        val wall2 = Wall(Position2D(2, 1))
        val wall3 = Wall(Position2D(3, 2))
        val wall4 = Wall(Position2D(2, 3))
        val map = board(10, 10)
        val dsl = MapDSL(map)

        import dsl.*

        place a spacMan at position(1, 1)
        place a ghost1 at position(2, 2)
        place a wall1 at position(1, 2)
        place a wall2 at position(2, 1)
        place a wall3 at position(3, 2)
        place a wall4 at position(2, 3)

        val gameManager = SimpleGameManager(spacMan, dsl.map)

        val movedGhosts = gameManager.moveGhosts()
        assert(movedGhosts.exists(_.position == Position2D(2, 2)))
        assert(gameManager.getGameMap.entityAt(Position2D(2, 2)).getOrElse(Set()).exists(_.isInstanceOf[GhostBasic]))

    it should "eat dot and increase score" in:
        val gameManager = createTestSetup()
        val movedSpacManOpt = gameManager.moveSpacManAndCheck(Direction.Right)
        assert(movedSpacManOpt.isDefined)
        val movedSpacMan = movedSpacManOpt.get
        assert(movedSpacMan.position == Position2D(2, 1))
        assert(gameManager.getGameMap.entityAt(Position2D(2, 1)).getOrElse(Set()).exists(_.isInstanceOf[SpacManBasic]))
        assert(movedSpacMan.score == DOT_BASIC_SCORE)

    it should "detect win condition" in:
        val gameManager = createTestSetup()
        assert(!gameManager.isWin())

        val movedSpacManOpt = gameManager.moveSpacManAndCheck(Direction.Right)
        assert(movedSpacManOpt.isDefined)
        val movedSpacMan = movedSpacManOpt.get
        assert(movedSpacMan.position == Position2D(2, 1))
        assert(gameManager.getGameMap.entityAt(Position2D(1, 1)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.entityAt(Position2D(2, 1)).getOrElse(Set()).exists(_.isInstanceOf[SpacManBasic]))
        assert(movedSpacMan.score == DOT_BASIC_SCORE)
        assert(!gameManager.isWin())

        val movedSpacManOpt2 = gameManager.moveSpacManAndCheck(Direction.Right)
        assert(movedSpacManOpt2.isDefined)
        val updatedSpacMan = movedSpacManOpt2.get
        assert(updatedSpacMan.position == Position2D(3, 1))
        assert(gameManager.getGameMap.entityAt(Position2D(2, 1)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.entityAt(Position2D(3, 1)).getOrElse(Set()).exists(_.isInstanceOf[SpacManBasic]))
        assert(updatedSpacMan.score == 2 * DOT_BASIC_SCORE)
        assert(gameManager.isWin())

    it should "set game over when colliding with a ghost" in:
        val gameManager = createTestSetup()
        val movedSpacManOpt = gameManager.moveSpacManAndCheck(Direction.Down)
        assert(gameManager.getGameMap.entityAt(Position2D(1, 1)).getOrElse(Set()).isEmpty)
        assert(gameManager.getGameMap.entityAt(Position2D(1, 2)).getOrElse(Set()).exists(!_.isInstanceOf[SpacManBasic]))
        assert(!movedSpacManOpt.isDefined)        
        assert(gameManager.isGameOver())