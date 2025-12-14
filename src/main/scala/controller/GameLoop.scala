package controller

import model.GameManager
import view.GameView
import scala.swing.Swing
import controller.GameState
import model.Direction

private final val DEFAULT_GHOST_DELAY_MS       = 350
private final val DEFAULT_GHOST_DELAY_CHASE_MS = 500
private final val DEFAULT_SPACMAN_DELAY_MS     = 250

/** Rapprenta il loop principale del gioco
  *
  * @param gameManager
  * @param inputManager
  * @param view
  */
case class GameLoop(gameManager: GameManager, inputManager: InputManager, view: GameView):
    // Salvato nelle variabili per la possibilità di modificarle in futuro
    // tramite moltiplicatori di velocità o power-up
    val ghostDelayNormal = DEFAULT_GHOST_DELAY_MS
    val ghostDelayChase  = DEFAULT_GHOST_DELAY_CHASE_MS
    val spacmanDelay     = DEFAULT_SPACMAN_DELAY_MS

    /** Funzione ricorsiva che rappresenta il loop principale del gioco
      * @param state Stato attuale del gioco
      * @param lastGhostMove Timestamp dell'ultimo movimento dei fantasmi
      * @param lastPacmanMove Timestamp dell'ultimo movimento dello SpacMan
      * @param lastUpdateTime Timestamp dell'ultimo aggiornamento del gioco
      * @return Lo stato finale del gioco (Win o GameOver)
      */
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

        val _ = gameManager.updateChaseTime(deltaTime)

        val currentGhostDelay =
            if gameManager.isChaseMode then ghostDelayChase else ghostDelayNormal

        state match
            case GameState.Running | GameState.Chase =>
                if isTimeToMove(now, lastGhostMove, currentGhostDelay) then
                    val _ = gameManager.moveGhosts()
                    leatestGhostMove = now
                if isTimeToMove(now, lastPacmanMove, spacmanDelay) then
                    val directionToMove = calculateSpacManDirection()
                    gameManager.moveSpacMan(directionToMove)
                    leatestSpacManMove = now
                    updateView()
                Thread.sleep(50)
                val newState = checkGameState(gameManager)
                loop(newState, leatestGhostMove, leatestSpacManMove, now)
            case finalState: GameState => finalState

    def checkGameState(gameManager: GameManager): GameState = gameManager match
        case gm if gm.isWin()      => GameState.Win
        case gm if gm.isGameOver() => GameState.GameOver
        case gm if gm.isChaseMode  => GameState.Chase
        case _                     => GameState.Running

    private def isTimeToMove(
        currTime: Long,
        lastMovableEntityMove: Long,
        entityDelay: Long
    ): Boolean = currTime - lastMovableEntityMove >= entityDelay

    /** Calcola la nuova direzione del SpacMan in base all'input ricevuto
      * @return La nuova direzione calcolata
      */
    private def calculateSpacManDirection(): Direction =
        inputManager.processInput() match
            case Some(dir) => dir
            case None      => gameManager.getState.spacMan.direction

    private def updateView(): Unit =
        val game = gameManager.getState
        Swing.onEDT:
            view.update(
              game.gameMap,
              game.spacMan.lives,
              game.spacMan.score
            )
