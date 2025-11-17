import model.GameManager
import java.util.Timer
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

enum GameState:
    case Running
    case Win
    case GameOver

object GameLoop:
    val ghost_delay   = 500
    val spacman_delay = 500

    def loop(
        gameManager: GameManager,
        state: GameState = GameState.Running,
        lastGhostMove: Long = System.currentTimeMillis(),
        lastPacmanMove: Long = System.currentTimeMillis(),
        inputManager: InputManager
    ): GameState =
        var leatestGhostMove   = lastGhostMove
        var leatestSpacManMove = lastPacmanMove
        val now                = System.currentTimeMillis()
        state match
            case GameState.Running =>
                // println("Start step ...")
                if now - lastGhostMove >= ghost_delay then
                    gameManager.moveGhosts()
                    leatestGhostMove = now
                if now - lastPacmanMove >= spacman_delay then
                    inputManager.processInput() match
                        case Some(dir)  => gameManager.moveSpacManAndCheck(dir)
                        case None       => // do nothing
                    leatestSpacManMove = now
                    // view.update(gameManager.gameMap)
                Thread.sleep(50)
                val newState = checkGameState(gameManager)
                loop(gameManager, newState, leatestGhostMove, leatestSpacManMove, inputManager)
            case finalState => finalState

    private def checkGameState(gameManager: GameManager): GameState =

        if gameManager.isWin() then
            println("WIN!")
            return GameState.Win
        if gameManager.isGameOver() then
            println("LOSE!")
            return GameState.GameOver
        GameState.Running

// Created only for demo purpose
private def createMap(): GameMap =
    val dsl    = MapDSL(board(10, 10))
    val dot    = DotBasic(Position2D(0, 0))
    val ghost1 = GhostBasic(Position2D(1, 1), Direction.Down, 1.0, 1)
    val ghost2 = GhostBasic(Position2D(2, 2), Direction.Up, 1.0, 2)
    import dsl.*

    place a genericWall() from position(4, 4) to position(6, 6)
    place a ghost1 at position(1, 1)
    place a ghost2 at position(2, 2)
    place a dot at position(0, 0)

    println(dsl.map.getWalls)
    dsl.map

@main def main(): Unit =
    val map         = createMap()
    val spacman     = SpacManBasic(Position2D(9, 9), Direction.Down, 0)
    val gameManager = SimpleGameManager(spacman, map)
    val inputManager = new SimpleInputManager(gameManager)
    
    inputManager.startInputThread()

    GameLoop.loop(gameManager, inputManager = inputManager)