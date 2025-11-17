import model.GameManager

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
        else if gameManager.isGameOver() then
            println("LOSE!")
            return GameState.GameOver
        else
            GameState.Running
