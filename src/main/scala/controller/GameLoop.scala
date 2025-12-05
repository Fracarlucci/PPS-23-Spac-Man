package controller

import model.GameManager
import model.InputManager
import view.GameView
import scala.swing.Swing
import controller.GameState

private final val DEFAULT_GHOST_DELAY_MS = 2000
private final val DEFAULT_SPACMAN_DELAY_MS = 500


case class GameLoop(gameManager: GameManager, inputManager: InputManager, view: GameView):
    val ghostDelay   = DEFAULT_GHOST_DELAY_MS
    val spacmanDelay = DEFAULT_SPACMAN_DELAY_MS

    def loop(
        state: GameState = GameState.Running,
        lastGhostMove: Long = System.currentTimeMillis(),
        lastPacmanMove: Long = System.currentTimeMillis()
    ): GameState =
        var leatestGhostMove   = lastGhostMove
        var leatestSpacManMove = lastPacmanMove
        val now                = System.currentTimeMillis()
        state match
            case GameState.Running =>
                if isTimeToMove(now, lastGhostMove, ghostDelay) then
                    gameManager.moveGhosts()
                    leatestGhostMove = now
                if isTimeToMove(now, lastPacmanMove, spacmanDelay) then
                    inputManager.processInput() match
                        case Some(dir) => gameManager.moveSpacManAndCheck(dir)
                        case None      => // do nothing
                    leatestSpacManMove = now
                    Swing.onEDT:
                        view.update(gameManager.getGameMap, gameManager.getSpacMan.lives, gameManager.getSpacMan.score)
                Thread.sleep(50)
                val newState = checkGameState(gameManager)
                loop(newState, leatestGhostMove, leatestSpacManMove)
            case finalState: GameState => finalState

    def checkGameState(gameManager: GameManager): GameState = gameManager match
        case gm if gm.isWin()       => GameState.Win
        case gm if gm.isGameOver()  => GameState.GameOver
        case _                      => GameState.Running

    private def isTimeToMove(
        currTime: Long,
        lastMovableEntityMove: Long,
        entityDelay: Long
    ): Boolean = currTime - lastMovableEntityMove >= entityDelay
