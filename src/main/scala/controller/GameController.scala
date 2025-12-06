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
        val (spacman, map) = createMap()
        val gameManager    = SimpleGameManager(spacman, map)
        val view           = SimpleSwingApp.create(gameManager.getGameMap)
        val inputManager   = SwingInputManager(view.getGamePanel)

        inputManager.start()

        val gameLoop = GameLoop(gameManager, inputManager, view)

        new Thread(() => {
            val finalState = gameLoop.loop()
            handleFinalState(finalState, gameManager, inputManager, view)
        }).start()

    private def createMap(): (SpacManWithLife, GameMap) =
        val ghostSpawnPoint = Position2D(1, 1)
        val dsl             = MapDSL(board(11, 11, Position2D(1, 1), ghostSpawnPoint))
        val ghost1          = GhostBasic(Position2D(7, 7), Direction.Down, 1.0, 1)
        val ghost2          = GhostBasic(Position2D(2, 2), Direction.Up, 1.0, 2)
        val spacman         = SpacManWithLife(Position2D(9, 9), Direction.Right, 0)
        val tunnel1         = Tunnel(Position2D(10, 5), Position2D(0, 5), Direction.Right)
        val tunnel2         = Tunnel(Position2D(0, 5), Position2D(10, 5), Direction.Left)
        val dotPower1       = DotPower(Position2D(9, 8))
        val dotPower2       = DotPower(Position2D(8, 9))

        import dsl.*

        place a genericWall() from position(4, 4) to position(6, 6)
        place a genericWall() from position(0, 0) to position(10, 0)
        place a genericWall() from position(0, 1) to position(0, 4)
        place a genericWall() from position(0, 6) to position(0, 10)
        place a genericWall() from position(10, 1) to position(10, 4)
        place a genericWall() from position(10, 6) to position(10, 10)
        place a genericWall() from position(0, 10) to position(9, 10)

        val wallPositions = getWallPositions()

        for
            x <- 1 until 10
            y <- 1 until 10
            pos = Position2D(x, y)
            if !wallPositions.contains(pos) && pos != spacman.position
                && pos != dotPower1.position && pos != dotPower2.position
                && !dsl.map.ghostSpawnPoints.contains(pos)
        do
            place a DotBasic(pos) at position(x, y)

        place a ghost1 at position(7, 7)
        place a ghost2 at position(2, 2)
        place a spacman at position(9, 9)
        place a tunnel1 at position(10, 5)
        place a tunnel2 at position(0, 5)
        place a dotPower1 at position(9, 8)
        place a dotPower2 at position(8, 9)

        (spacman, dsl.map)

    private def getWallPositions(): Set[Position2D] =
        val positions = Set.newBuilder[Position2D]

        for x <- 0 to 10 do positions += Position2D(x, 0)
        for y <- 1 to 4 do positions += Position2D(0, y)
        for y <- 6 to 10 do positions += Position2D(0, y)
        for y <- 1 to 4 do positions += Position2D(10, y)
        for y <- 6 to 10 do positions += Position2D(10, y)
        for x <- 0 to 9 do positions += Position2D(x, 10)

        for
            x <- 4 to 6
            y <- 4 to 6
        do positions += Position2D(x, y)

        positions.result()

    private def getGhostSpawnPositions(spawnPoint: Position2D): Set[Position2D] =
        val positions = Set.newBuilder[Position2D]
        for
            x <- spawnPoint.x to (spawnPoint.x + 1)
            y <- spawnPoint.y to (spawnPoint.y + 1)
        do positions += Position2D(x, y)
        positions.result()

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
