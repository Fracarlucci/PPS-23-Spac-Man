package controller

import model.GameManager
import model.InputManager
import view.GameView
import scala.swing.Swing
import controller.GameState

case class GameLoop(gameManager: GameManager, inputManager: InputManager, view: GameView):
    val ghostDelayNormal = 500
    val ghostDelayChase  = 800
    val spacmanDelay     = 250

    def loop(
        state: GameState = GameState.Running,
        lastGhostMove: Long = System.currentTimeMillis(),
        lastPacmanMove: Long = System.currentTimeMillis(),
        lastUpdateTime: Long = System.currentTimeMillis()
    ): GameState =
        var leatestGhostMove   = lastGhostMove
        var leatestSpacManMove = lastPacmanMove
        val now                = System.currentTimeMillis()
        val deltaTime          = now - lastUpdateTime

        gameManager.updateChaseTime(deltaTime)

        val currentGhostDelay =
            if gameManager.isChaseMode then ghostDelayChase else ghostDelayNormal

        state match
            case GameState.Running | GameState.Chase =>
                if isTimeToMove(now, lastGhostMove, currentGhostDelay) then
                    gameManager.moveGhosts()
                    leatestGhostMove = now
                if isTimeToMove(now, lastPacmanMove, spacmanDelay) then
                    inputManager.processInput() match
                        case Some(dir) => gameManager.moveSpacManAndCheck(dir)
                        case None      => // do nothing
                    leatestSpacManMove = now
                    Swing.onEDT:
                        view.update(
                          gameManager.getGameMap,
                          gameManager.getSpacMan.lives,
                          gameManager.getSpacMan.score
                        )
                Thread.sleep(50)
                val newState = checkGameState(gameManager)
                loop(newState, leatestGhostMove, leatestSpacManMove, now)
            case finalState => finalState

    def checkGameState(gameManager: GameManager): GameState =
        if gameManager.isWin() then GameState.Win
        else if gameManager.isGameOver() then GameState.GameOver
        else if gameManager.isChaseMode then GameState.Chase
        else GameState.Running

    private def isTimeToMove(
        currTime: Long,
        lastMovableEntityMove: Long,
        entityDelay: Long
    ): Boolean = currTime - lastMovableEntityMove >= entityDelay
