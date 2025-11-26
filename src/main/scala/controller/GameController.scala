package controller

import model.MapDSL
import model.board
import model.genericWall
import model.DotBasic
import model.Position2D
import model.GhostBasic
import model.Direction
import model.map.GameMap
import model.GameManager
import model.SimpleGameManager
import model.InputManager
import model.SimpleInputManager
import model.SpacManBasic
import view.GameView
import view.SimpleSwingApp
import scala.swing.Frame
import view.{GameView, SimpleSwingApp}
import scala.swing.Swing
import java.awt.event.{KeyAdapter, KeyEvent}
import controller.GameLoop
import model.Tunnel

enum GameState:
    case Menu
    case Running
    case Win
    case GameOver

object GameController:
    def showHome(): Unit =
        val homeView = new view.HomeView()
        homeView.visible = true

    def startGame(): Unit =
        val (spacman, map) = createMap()
        val gameManager    = SimpleGameManager(spacman, map)
        val inputManager   = SimpleInputManager()
        val view           = SimpleSwingApp.create(gameManager.getGameMap)

        inputManager.startInputThread()

        val gameLoop = GameLoop(gameManager, inputManager, view)

        new Thread(() => {
            val finalState = gameLoop.loop()
            handleFinalState(finalState, gameManager, inputManager, view)
        }).start()

    private def createMap(): (SpacManBasic, GameMap) =
        val dsl     = MapDSL(board(200, 200))
        val dot     = DotBasic(Position2D(0, 0))
        val ghost1  = GhostBasic(Position2D(1, 1), Direction.Down, 1.0, 1)
        val ghost2  = GhostBasic(Position2D(2, 2), Direction.Up, 1.0, 2)
        val spacman = SpacManBasic(Position2D(9, 9), Direction.Down, 0)
        val tunnel1  = Tunnel(Position2D(10, 6), Position2D(5, 10), Direction.Right)
        val tunnel2  = Tunnel(Position2D(5, 10), Position2D(10, 6), Direction.Down)

        import dsl.*

        place a genericWall() from position(4, 4) to position(6, 6)
        place a ghost1 at position(1, 1)
        place a ghost2 at position(2, 2)
        place a dot at position(0, 0)
        place a spacman at position(9, 9)
        place a tunnel1 at position(10, 6)
        place a tunnel2 at position(5, 10)
        place a genericWall() from position(10, 0) to position(10, 5)
        place a genericWall() from position(10, 7) to position(10, 9)
        place a genericWall() from position(0, 10) to position(4, 10)
        place a genericWall() from position(10, 10) to position(6, 10)
        
        (spacman, dsl.map)

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
