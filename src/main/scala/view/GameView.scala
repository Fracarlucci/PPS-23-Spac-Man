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

    private val titleLabel = new Label("SpacMan"):
        horizontalAlignment = Alignment.Center
        font = new Font("Arial", java.awt.Font.BOLD, 36)
    private val startButton = new Button("Start"):
        preferredSize = new Dimension(100, 50)
        font = new Font("Arial", java.awt.Font.BOLD, 20)
    private val mainPanel = new BoxPanel(Orientation.Vertical):
        contents += Swing.VStrut(100) 
        contents += new FlowPanel(titleLabel)
        contents += Swing.VStrut(40) 
        contents += new FlowPanel(startButton)

    contents = new BorderPanel {
        layout(mainPanel) = BorderPanel.Position.Center
    }

    private def createControlPanel(startGameButton: Button): FlowPanel = {
        new FlowPanel {
            hGap = 5
            vGap = 5
            border = Swing.EmptyBorder(2, 0, 2, 0)

            listenTo(startGameButton)
            // startGameButton.reactions += {
            //     case ButtonClicked(_) => controller.startGame()
            // }
            contents += startGameButton
        }
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

    def displayWin(score: Int): Unit      = ???
    def displayGameOver(score: Int): Unit = ???

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
                        case ghost: GhostBasic =>
                            drawSprite(g, "ghost_pinky", x, y)
                        case _ => ()
                    }
                case Left(_) => ()

    private def drawPacman(dir: Direction): String = dir match
        case Direction.Right => "pacman_right"
        case Direction.Left  => "pacman_left"
        case Direction.Up    => "pacman_up"
        case Direction.Down  => "pacman_down"

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
