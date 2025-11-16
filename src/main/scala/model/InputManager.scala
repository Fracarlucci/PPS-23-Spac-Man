
import java.util.concurrent.ConcurrentLinkedQueue
import scala.io.StdIn
import model.Direction
import model.GameManager
import java.util.concurrent.ConcurrentLinkedQueue
import scala.io.StdIn

import java.util.Timer
import model.MapDSL
import model.board
import model.genericWall
import model.DotBasic
import model.Position2D
import model.GhostBasic
import model.Direction
import model.map.GameMap
import model.SimpleGameManager
import model.SpacManBasic

import org.jline.terminal.TerminalBuilder

class InputManager(gameManager: GameManager):
    @volatile private var pendingMove: Option[Direction] = None
    @volatile private var running = true
    private var terminal: org.jline.terminal.Terminal = _

    private def handleKey(key: Int): Unit = 
        key.toChar.toLower match
            case 'w' => pendingMove = Some(Direction.Up)
            case 'a' => pendingMove = Some(Direction.Left)
            case 's' => pendingMove = Some(Direction.Down)
            case 'd' => pendingMove = Some(Direction.Right)
            case 'q' => running = false
            case _   => // ignore invalid keys

    def isRunning: Boolean = running

    def stop(): Unit = 
        running = false
        if terminal != null then
            terminal.close()
    
    def moveSpacMan(): Unit =
        pendingMove.foreach { dir =>
            println(s"Moving SpacMan in direction: $dir")
            gameManager.moveSpacManAndCheck(dir)
            pendingMove = None 
        }
    
    def startInputThread(): Thread =
        val thread = new Thread(() => {
            terminal = TerminalBuilder.builder()
                .system(true)
                .build()
            terminal.enterRawMode()
            
            val reader = terminal.reader()
            while running do
                try
                    if reader.peek(10) > 0 then
                        val key = reader.read()
                        handleKey(key)
                catch
                    case _: Exception =>
            
            terminal.close()
        })
        thread.setDaemon(true)
        thread.start()
        thread
    