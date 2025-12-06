package controller

import model.GameManager
import model.InputManager
import view.GameView
import scala.swing.Swing
import controller.GameState

private final val DEFAULT_GHOST_DELAY_MS = 500
private final val DEFAULT_GHOST_DELAY_CHASE_MS = 800
private final val DEFAULT_SPACMAN_DELAY_MS = 250


case class GameLoop(gameManager: GameManager, inputManager: InputManager, view: GameView):
    val ghostDelayNormal = DEFAULT_GHOST_DELAY_MS
    val ghostDelayChase  = DEFAULT_GHOST_DELAY_CHASE_MS
    val spacmanDelay     = DEFAULT_SPACMAN_DELAY_MS

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
            case finalState: GameState => finalState

    def checkGameState(gameManager: GameManager): GameState = gameManager match
        case gm if gm.isWin()       => GameState.Win
        case gm if gm.isGameOver()  => GameState.GameOver
        case gm if gm.isChaseMode   => GameState.Chase
        case _                      => GameState.Running

    private def isTimeToMove(
        currTime: Long,
        lastMovableEntityMove: Long,
        entityDelay: Long
    ): Boolean = currTime - lastMovableEntityMove >= entityDelay
