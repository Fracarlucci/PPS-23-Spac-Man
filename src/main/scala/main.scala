import model.MapDSL
import model.board
import model.genericWall
import model.DotBasic
import model.Position2D
import model.GhostBasic
import model.Direction
import model.map.GameMap
import model.SimpleGameManager
import model.SpacManBasic

private def createMap(): (SpacManBasic, GameMap) =
    val dsl     = MapDSL(board(10, 10))
    val dot     = DotBasic(Position2D(0, 0))
    val ghost1  = GhostBasic(Position2D(1, 1), Direction.Down, 1.0, 1)
    val ghost2  = GhostBasic(Position2D(2, 2), Direction.Up, 1.0, 2)
    val spacman = SpacManBasic(Position2D(9, 9), Direction.Down, 0)
    import dsl.*

    place a genericWall() from position(4, 4) to position(6, 6)
    place a ghost1 at position(1, 1)
    place a ghost2 at position(2, 2)
    place a dot at position(0, 0)
    place a spacman at position(9, 9)

    (spacman, dsl.map)

@main def main(): Unit =
    val (spacman, map) = createMap()
    val gameManager = SimpleGameManager(spacman, map)
    val inputManager = SimpleInputManager(gameManager)
    
    inputManager.startInputThread()

    GameLoop.loop(gameManager, inputManager = inputManager)

    inputManager.stop()
