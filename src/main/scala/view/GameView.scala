package view

import scala.swing.SimpleSwingApplication
import scala.swing.MainFrame
import scala.swing.Frame
import scala.swing.BoxPanel
import scala.swing.Orientation
import scala.swing.Label
import scala.swing.Button
import scala.swing.Swing
import scala.swing.Dimension
import scala.swing.FlowPanel
import scala.swing.event.ButtonClicked
import scala.swing.BorderPanel
import scala.swing.Font
import scala.swing.Alignment
import javax.swing.JPanel
import java.awt.CardLayout
import javax.swing.JFrame
import model.map.GameMap
import scala.swing.Panel
import scala.swing.Graphics2D
import model.Position2D
import model.DotBasic
import model.Wall
import model.SpacManBasic
import model.Direction
import model.GhostBasic
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.awt.Color
import controller.GameController
import model.Tunnel

object SimpleSwingApp {
    def create(map: GameMap): GameView =
        val frame = new GameView(map)
        frame.initialize()
        frame.visible = true
        frame
}

class HomeView() extends MainFrame:
    private final val SIZE = 1000
    title = "SpacMan"
    preferredSize = new Dimension(SIZE, 500)

    peer.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE)

    private val titleLabel = new Label("SpacMan"):
        horizontalAlignment = Alignment.Center
        font = new Font("Arial", java.awt.Font.BOLD, 36)
    private val startButton = new Button("Start"):
        preferredSize = new Dimension(100, 50)
        font = new Font("Arial", java.awt.Font.BOLD, 20)

    listenTo(startButton)
    reactions += {
        case ButtonClicked(`startButton`) =>
            visible = false
            dispose()
            GameController.startGame()
    }

    private val mainPanel = new BoxPanel(Orientation.Vertical):
        contents += Swing.VStrut(100)
        contents += new FlowPanel(titleLabel)
        contents += Swing.VStrut(40)
        contents += new FlowPanel(startButton)

    contents = new BorderPanel {
        layout(mainPanel) = BorderPanel.Position.Center
    }

class GameView(gameMap: GameMap) extends MainFrame:
    private val gamePanel = GameMapPanel(gameMap)

    def initialize(): Unit =
        val SIZE = 1000
        title = "SpacMan"
        preferredSize = new Dimension(SIZE, SIZE)
        contents = new BorderPanel {
            layout(gamePanel) = BorderPanel.Position.Center
        }

    def update(map: GameMap): Unit =
        gamePanel.updateMap(map)

    def displayWin(score: Int): Unit =
        val titleLabel = new Label("Hai vinto!"):
            horizontalAlignment = Alignment.Center
            font = new Font("Arial", java.awt.Font.BOLD, 36)
        val scoreLabel = new Label("Score: " + score):
            horizontalAlignment = Alignment.Center
            font = new Font("Arial", java.awt.Font.PLAIN, 25)
        val homeButton = new Button("Torna alla home"):
            preferredSize = new Dimension(300, 50)
            font = new Font("Arial", java.awt.Font.BOLD, 20)

        listenTo(homeButton)
        reactions += {
            case ButtonClicked(`homeButton`) =>
                visible = false
                dispose()
                GameController.showHome()
        }

        val winPanel = new BoxPanel(Orientation.Vertical):
            contents += Swing.VStrut(100)
            contents += new FlowPanel(titleLabel)
            contents += new FlowPanel(scoreLabel)
            contents += Swing.VStrut(40)
            contents += new FlowPanel(homeButton)

        contents = new BorderPanel {
            layout(winPanel) = BorderPanel.Position.Center
        }

    def displayGameOver(score: Int): Unit =
        val titleLabel = new Label("Hai perso"):
            horizontalAlignment = Alignment.Center
            font = new Font("Arial", java.awt.Font.BOLD, 36)
        val scoreLabel = new Label("Score: " + score):
            horizontalAlignment = Alignment.Center
            font = new Font("Arial", java.awt.Font.PLAIN, 25)
        val homeButton = new Button("Torna alla home"):
            preferredSize = new Dimension(300, 50)
            font = new Font("Arial", java.awt.Font.BOLD, 20)

        listenTo(homeButton)
        reactions += {
            case ButtonClicked(`homeButton`) =>
                visible = false
                dispose()
                GameController.showHome()
        }

        val winPanel = new BoxPanel(Orientation.Vertical):
            contents += Swing.VStrut(100)
            contents += new FlowPanel(titleLabel)
            contents += new FlowPanel(scoreLabel)
            contents += Swing.VStrut(40)
            contents += new FlowPanel(homeButton)

        contents = new BorderPanel {
            layout(winPanel) = BorderPanel.Position.Center
        }

class GameMapPanel(private var gameMap: GameMap) extends Panel:
    private val CELL_SIZE = 32

    SpriteLoader.preloadAll()

    preferredSize = new Dimension(
      gameMap.width * CELL_SIZE,
      gameMap.height * CELL_SIZE
    )

    background = Color.BLACK
    focusable = true

    override def paintComponent(g: Graphics2D): Unit =
        super.paintComponent(g)

        g.setRenderingHint(
          java.awt.RenderingHints.KEY_INTERPOLATION,
          java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR
        )

        g.setColor(Color.BLACK)
        g.fillRect(0, 0, size.width, size.height)

        for
            y <- 0 until gameMap.height
            x <- 0 until gameMap.width
        do
            val pos = Position2D(x, y)
            gameMap.entityAt(pos) match
                case Right(entities) =>
                    entities.foreach {
                        case dot: DotBasic =>
                            drawSprite(g, "dot", x, y)
                        case wall: Wall =>
                            drawSprite(g, "wall", x, y)
                        case pacman: SpacManBasic =>
                            drawSprite(g, drawPacman(pacman.direction), x, y)
                        case tunnel: Tunnel =>
                            drawSprite(g, drawTunnel(tunnel.correctDirection), x, y)
                        case ghost: GhostBasic =>
                            drawSprite(g, "ghost_pinky", x, y)
                        case _ => ()
                    }
                case Left(_) => ()

    private def drawPacman(dir: Direction): String = drawDirectables(dir, "pacman")

    private def drawTunnel(dir: Direction): String = drawDirectables(dir, "arrow")

    private def drawDirectables(dir: Direction, subject: String): String = dir match
        case Direction.Right => subject + "_right"
        case Direction.Left  => subject + "_left"
        case Direction.Up    => subject + "_up"
        case Direction.Down  => subject + "_down"

    private def drawSprite(g: Graphics2D, name: String, x: Int, y: Int): Unit =
        SpriteLoader.load(name) match
            case Some(sprite) =>
                g.drawImage(
                  sprite,
                  x * CELL_SIZE,
                  y * CELL_SIZE,
                  CELL_SIZE,
                  CELL_SIZE,
                  null
                )
            case None =>
                g.setColor(Color.GRAY)
                g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE)

    def updateMap(newMap: GameMap): Unit =
        gameMap = newMap
        repaint()
