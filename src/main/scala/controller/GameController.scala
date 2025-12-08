package controller

import model.MapDSL
import model.board
import model.DotBasic
import model.Position2D
import model.GhostBasic
import model.Direction
import model.map.GameMap
import model.GameManager
import model.SimpleGameManager
import model.InputManager
import model.SwingInputManager
import model.SpacManWithLife
import view.GameView
import view.SimpleSwingApp
import scala.swing.Frame
import view.{GameView, SimpleSwingApp}
import scala.swing.Swing
import java.awt.event.{KeyAdapter, KeyEvent}
import controller.GameLoop
import model.Tunnel
import model.DotPower
import model.DotFruit
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
        val view           = SimpleSwingApp.create(gameManager.getGameMap)
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
                    view.displayWin(gameManager.getSpacMan.score)
            case GameState.GameOver =>
                Swing.onEDT:
                    view.displayGameOver(gameManager.getSpacMan.score)
            case _ => ()
