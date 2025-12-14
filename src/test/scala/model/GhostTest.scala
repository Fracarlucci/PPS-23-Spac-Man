package model

import org.scalatest.flatspec.AnyFlatSpec
import model.map.GameMapImpl

class GhostTest extends AnyFlatSpec:
    val startposition  = Position2D(0, 0)
    val startDirection = Direction.Right
    val id             = 1
    val ghost          = GhostBasic(startposition, startDirection, id)

    "GhostBasic" should "create a ghost with correct properties" in:
        assert(
          ghost.position == startposition &&
          ghost.direction == startDirection &&
          ghost.id == id &&
          ghost.score == 100
        )

    it should "update position and direction with withPosAndDir" in:
        val newPos       = Position2D(5, 5)
        val newDir       = Direction.Left
        val updatedGhost = ghost.withPosAndDir(newPos, newDir)

        assert(updatedGhost.position == newPos)
        assert(updatedGhost.direction == newDir)
        assert(updatedGhost.id == id)
        assert(updatedGhost.score == 100)

    it should "always return a Direction from nextMove" in:
        val gameMap    = GameMapImpl(10, 10, Position2D(0, 0), Position2D(1, 1), Map.empty)
        val spacManPos = Position2D(5, 5)
        val spacManDir = Direction.Up

        val ghost1 = GhostBasic(Position2D(3, 3), Direction.Right, 1)
        val ghost2 = GhostBasic(Position2D(3, 3), Direction.Right, 2)
        val ghost3 = GhostBasic(Position2D(3, 3), Direction.Right, 3)
        val ghost4 = GhostBasic(Position2D(3, 3), Direction.Right, 4)

        assert(ghost1.nextMove(spacManPos, spacManDir, gameMap).isInstanceOf[Direction])
        assert(ghost2.nextMove(spacManPos, spacManDir, gameMap).isInstanceOf[Direction])
        assert(ghost3.nextMove(spacManPos, spacManDir, gameMap).isInstanceOf[Direction])
        assert(ghost4.nextMove(spacManPos, spacManDir, gameMap).isInstanceOf[Direction])

    "GhostBehavior.forId" should "return ChaseBehavior for id 1" in:
        assert(GhostBehavior.forId(1) == ChaseBehavior)

    it should "return PredictiveBehavior for id 2" in:
        assert(GhostBehavior.forId(2) == PredictiveBehavior)

    it should "return RandomBehavior for id 3" in:
        assert(GhostBehavior.forId(3) == RandomBehavior)

    it should "return MixedBehavior for id 4" in:
        assert(GhostBehavior.forId(4) == MixedBehavior)

    it should "return ChaseBehavior for any other id" in:
        assert(GhostBehavior.forId(100) == ChaseBehavior)
        assert(GhostBehavior.forId(0) == ChaseBehavior)
        assert(GhostBehavior.forId(-1) == ChaseBehavior)

    // Behaviours tests
    "Behaviours" should "return the current direction when the ghost is completely blocked" in:
        val gameMap    = GameMapImpl(1, 1, Position2D(1, 1), Position2D(0, 0), Map.empty)
        val ghost      = GhostBasic(Position2D(1, 1), Direction.Right, 1)

        var direction =
            ChaseBehavior.chooseDirection(GhostContext(ghost, Position2D(2, 2), Direction.Right, gameMap))
        assert(direction == Direction.Right)

        direction =
            PredictiveBehavior.chooseDirection(GhostContext(ghost, Position2D(2, 2), Direction.Right, gameMap))
        assert(direction == Direction.Right)

        direction =
            RandomBehavior.chooseDirection(GhostContext(ghost, Position2D(2, 2), Direction.Right, gameMap))
        assert(direction == Direction.Right)

        direction =
            MixedBehavior.chooseDirection(GhostContext(ghost, Position2D(2, 2), Direction.Right, gameMap))
        assert(direction == Direction.Right)

    "ChaseBehavior" should "choose the right direction to go to SpacMan" in:
        val gameMap    = GameMapImpl(10, 10, Position2D(0, 0), Position2D(1, 1), Map.empty)
        val ghost      = GhostBasic(Position2D(0, 0), Direction.Right, 1)
        val spacManPos = Position2D(5, 0)

        val direction =
            ChaseBehavior.chooseDirection(GhostContext(ghost, spacManPos, Direction.Right, gameMap))
        assert(direction == Direction.Right)

    "PredictiveBehavior" should "choose the right direction to go to the predicted position of SpacMan" in:
        val gameMap    = GameMapImpl(10, 10, Position2D(0, 0), Position2D(1, 1), Map.empty)
        val ghost      = GhostBasic(Position2D(0, 0), Direction.Right, 2)
        val spacManPos = Position2D(9, 1)
        val spacManDir = Direction.Left

        val direction =
            PredictiveBehavior.chooseDirection(GhostContext(ghost, spacManPos, spacManDir, gameMap))
        assert(direction == Direction.Left || direction == Direction.Down)

    "RandomBehavior" should "continue in same direction if it's not blocked" in:
        val gameMap = GameMapImpl(10, 10, Position2D(0, 0), Position2D(1, 1), Map.empty)
        val ghost   = GhostBasic(Position2D(5, 5), Direction.Right, 3)

        val direction =
            RandomBehavior.chooseDirection(GhostContext(ghost, Position2D(0, 0), Direction.Up, gameMap))
        assert(direction == Direction.Right)

    it should "choose a random direction if it's blocked" in:
        val gameMap = GameMapImpl(10, 10, Position2D(0, 0), Position2D(1, 1), Map.empty)
        val ghost   = GhostBasic(Position2D(9, 5), Direction.Right, 3)

        val direction =
            RandomBehavior.chooseDirection(GhostContext(ghost, Position2D(0, 0), Direction.Up, gameMap))
        assert(direction != Direction.Right)

    "MixedBehavior" should "chase when distance is greater than threshold" in:
        val gameMap    = GameMapImpl(10, 10, Position2D(0, 0), Position2D(1, 1), Map.empty)
        val ghost      = GhostBasic(Position2D(0, 0), Direction.Right, 4)
        val spacManPos = Position2D(9, 0)

        val direction =
            MixedBehavior.chooseDirection(GhostContext(ghost, spacManPos, Direction.Right, gameMap))
        assert(direction == Direction.Right)

    it should "flee when distance is less than or equal to threshold" in:
        val gameMap    = GameMapImpl(10, 10, Position2D(0, 0), Position2D(1, 1), Map.empty)
        val ghost      = GhostBasic(Position2D(0, 0), Direction.Right, 4)
        val spacManPos = Position2D(3, 0)

        val direction =
            MixedBehavior.chooseDirection(GhostContext(ghost, spacManPos, Direction.Right, gameMap))
        assert(direction == Direction.Down)
