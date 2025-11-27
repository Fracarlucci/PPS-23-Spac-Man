package controller

import model.GameManager
import model.InputManager
import view.GameView
import scala.swing.Swing
import controller.GameState

case class GameLoop(gameManager: GameManager, inputManager: InputManager, view: GameView):
    val ghostDelay   = 2000
    val spacmanDelay = 500

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
                        view.update(gameManager.getGameMap)
                Thread.sleep(50)
                val newState = checkGameState(gameManager)
                loop(newState, leatestGhostMove, leatestSpacManMove)
            case finalState => finalState

    def checkGameState(gameManager: GameManager): GameState =
        if gameManager.isWin() then GameState.Win
        else if gameManager.isGameOver() then GameState.GameOver
        else GameState.Running

    private def isTimeToMove(
        currTime: Long,
        lastMovableEntityMove: Long,
        entityDelay: Long
    ): Boolean = currTime - lastMovableEntityMove >= entityDelay
