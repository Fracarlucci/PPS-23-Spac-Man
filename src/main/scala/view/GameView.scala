package view

import scala.swing.MainFrame
import scala.swing.BoxPanel
import scala.swing.Orientation
import scala.swing.Swing
import scala.swing.Dimension
import scala.swing.FlowPanel
import scala.swing.event.ButtonClicked
import scala.swing.BorderPanel
import model.map.GameMap
import scala.swing.Panel
import scala.swing.Graphics2D
import model.Position2D
import model.DotBasic
import model.Wall
import model.Direction
import model.GhostBasic
import java.awt.image.BufferedImage
import java.awt.Color
import java.awt.Font
import controller.GameController
import model.Tunnel
import model.SpacManWithLife
import model.DotPower
import model.DotFruit

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

    private val titleLabel  = LabelFactory.createTitleLabel("SpacMan")
    private val startButton = ButtonFactory("Start", ButtonFactory.dimension(ButtonSize.Normal))

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
    private val infoPanel = new InfoPanel(3, 0)
    private val SIZE      = 1000

    def initialize(): Unit =
        title = "SpacMan"
        preferredSize = new Dimension(SIZE, SIZE + infoPanel.INFO_VERTICAL_SIZE)
        contents = new BorderPanel {
            layout(infoPanel) = BorderPanel.Position.North
            layout(gamePanel) = BorderPanel.Position.Center
        }

    def update(map: GameMap, lives: Int, score: Int): Unit =
        infoPanel.updateInfo(lives, score)
        gamePanel.updateMap(map)

    def getGamePanel: javax.swing.JComponent = gamePanel.peer

    def displayWin(score: Int): Unit =
        displayEndingScreen("Hai vinto!", score)

    def displayGameOver(score: Int): Unit =
        displayEndingScreen("Hai perso!", score)

    private def displayEndingScreen(titleText: String, score: Int): Unit =
        val titleLabel = LabelFactory.createTitleLabel(titleText)
        val scoreLabel = LabelFactory.createScoreLabel(score)
        val homeButton = ButtonFactory("Torna alla home", ButtonFactory.dimension(ButtonSize.Big))

        listenTo(homeButton)
        reactions += {
            case ButtonClicked(`homeButton`) =>
                visible = false
                dispose()
                GameController.showHome()
        }

        val endingPanel = new BoxPanel(Orientation.Vertical):
            contents += Swing.VStrut(100)
            contents += new FlowPanel(titleLabel)
            contents += new FlowPanel(scoreLabel)
            contents += Swing.VStrut(40)
            contents += new FlowPanel(homeButton)

        contents = new BorderPanel {
            layout(endingPanel) = BorderPanel.Position.Center
        }

class InfoPanel(private var lives: Int, private var score: Int) extends Panel:
    final val INFO_SIZE = 1000
    final val INFO_VERTICAL_SIZE = 60 
    private final val HEART_SIZE = 32
    private final val PADDING = 10
    
    preferredSize = new Dimension(INFO_SIZE, INFO_VERTICAL_SIZE)
    background = Color.BLACK
    
    override def paintComponent(g: Graphics2D): Unit =
        super.paintComponent(g)
        
        g.setColor(Color.BLACK)
        g.fillRect(0, 0, size.width, size.height)
        
        for i <- 0 until lives do
            SpriteLoader.load("life") match
                case Some(sprite) =>
                    g.drawImage(
                        sprite,
                        PADDING + (i * (HEART_SIZE + 5)),
                        PADDING,
                        HEART_SIZE,
                        HEART_SIZE,
                        null
                    )
                case None =>
                    g.setColor(Color.RED)
                    g.fillOval(PADDING + (i * (HEART_SIZE + 5)), PADDING, HEART_SIZE, HEART_SIZE)

        g.setColor(Color.WHITE)
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24))
        g.drawString(s"Score: $score", size.width - 200, 35)
        g.drawString(s"Score: $score", size.width - 200, 35)
    
    def updateInfo(newLives: Int, newScore: Int): Unit =
        lives = newLives
        score = newScore
        repaint()

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
                        case dotPower: DotPower =>
                            drawSprite(g, "dot_power", x, y)
                        case fruit: DotFruit =>
                            drawSprite(g, "apple", x, y)
                        case wall: Wall =>
                            drawSprite(g, "wall", x, y)
                        case pacman: SpacManWithLife =>
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
