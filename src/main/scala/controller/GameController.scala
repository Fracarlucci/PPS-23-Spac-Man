package controller

import model.map.GameMap
import model.GameManager
import model.SimpleGameManager
import model.SpacManWithLife
import view.{GameView, SimpleSwingApp}
import scala.swing.Swing
import model.map.MapLevel1

enum GameState:
    case Menu
    case Running
    case Chase
    case Win
    case GameOver

object GameController:
    def showHome(): Unit =
        val homeView = new view.HomeView()
        homeView.visible = true

    def startGame(): Unit =
        val (spacman, map) = MapLevel1.getMap()
        val gameManager    = SimpleGameManager(spacman, map)
        val view           = SimpleSwingApp.create(gameManager.getState.gameMap)
        val inputManager   = SwingInputManager(view.getGamePanel)

        inputManager.start()

        val gameLoop = GameLoop(gameManager, inputManager, view)

        new Thread(() => {
            val finalState = gameLoop.loop()
            handleFinalState(finalState, gameManager, inputManager, view)
        }).start()

    private def handleFinalState(
        state: GameState,
        gameManager: GameManager,
        inputManager: InputManager,
        view: GameView
    ): Unit =
        inputManager.stop()
        state match
            case GameState.Win =>
                Swing.onEDT:
                    view.displayWin(gameManager.getState.spacMan.score)
            case GameState.GameOver =>
                Swing.onEDT:
                    view.displayGameOver(gameManager.getState.spacMan.score)
            case _ => ()
