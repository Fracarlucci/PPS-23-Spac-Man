package controller

import model.GameManager
import view.GameView
import scala.swing.Swing
import model.Direction

private final val DEFAULT_GHOST_DELAY_MS       = 350
private final val DEFAULT_GHOST_DELAY_CHASE_MS = 500
private final val DEFAULT_SPACMAN_DELAY_MS     = 250

/** Represents the main game loop
  *
  * @param gameManager
  * @param inputManager
  * @param view
  */
case class GameLoop(gameManager: GameManager, inputManager: InputManager, view: GameView):
    /** Saved delays in case we want to customize them in the future */
    val ghostDelayNormal = DEFAULT_GHOST_DELAY_MS
    val ghostDelayChase  = DEFAULT_GHOST_DELAY_CHASE_MS
    val spacmanDelay     = DEFAULT_SPACMAN_DELAY_MS

    /** Main game loop method
      * @param state Current game state
      * @param lastGhostMove Timestamp of the last ghost move
      * @param lastPacmanMove Timestamp of the last SpacMan move
      * @param lastUpdateTime Timestamp of the last update
      * @return Final game state when the game ends
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

        gameManager.updateChaseTime(deltaTime)

        val currentGhostDelay =
            if gameManager.isChaseMode then ghostDelayChase else ghostDelayNormal

        state match
            case GameState.Running | GameState.Chase =>
                if isTimeToMove(now, lastGhostMove, currentGhostDelay) then
                    gameManager.moveGhosts()
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

    /** Controls the current game state
      * @param gameManager Current game manager
      * @return Current game state
      */
    def checkGameState(gameManager: GameManager): GameState = gameManager match
        case gm if gm.isWin()      => GameState.Win
        case gm if gm.isGameOver() => GameState.GameOver
        case gm if gm.isChaseMode  => GameState.Chase
        case _                     => GameState.Running

    /** Checks if it's time for an entity to move based on its delay
      * @param currTime Current timestamp
      * @param lastMovableEntityMove Timestamp of the last move
      * @param entityDelay Delay of the entity
      * @return True if it's time to move, false otherwise
      */
    private def isTimeToMove(
        currTime: Long,
        lastMovableEntityMove: Long,
        entityDelay: Long
    ): Boolean = currTime - lastMovableEntityMove >= entityDelay

    /** Calculates the next direction for SpacMan based on user input
      * @return Next direction for SpacMan
      */
    private def calculateSpacManDirection(): Direction =
        inputManager.processInput() match
            case Some(dir) => dir
            case None      => gameManager.getState.spacMan.direction

    /** Updates the game view */
    private def updateView(): Unit =
        val game = gameManager.getState
        Swing.onEDT:
            view.update(
              game.gameMap,
              game.spacMan.lives,
              game.spacMan.score
            )
