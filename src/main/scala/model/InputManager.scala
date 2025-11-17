
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ConcurrentLinkedQueue
import org.jline.terminal.TerminalBuilder
import model.Direction
import model.map.GameMap
import model.GameManager

trait InputManager:
    def startInputThread(): Thread
    def processInput(): Option[Direction]
    def stop(): Unit

class SimpleInputManager(gameManager: GameManager) extends InputManager:
    @volatile private var pendingMove: Option[Direction] = None
    @volatile private var running = true
    private var terminal: org.jline.terminal.Terminal = _

    private def handleKey(key: Int): Unit = 
        key.toChar.toLower match
            case 'w' => pendingMove = Some(Direction.Up)
            case 'a' => pendingMove = Some(Direction.Left)
            case 's' => pendingMove = Some(Direction.Down)
            case 'd' => pendingMove = Some(Direction.Right)
            case _   => // ignore invalid keys

    def processInput(): Option[Direction] =
        pendingMove match {
            case Some(dir) =>
                pendingMove = None
                Some(dir)
            case None => Option.empty
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
    
    def stop(): Unit = 
        running = false
        if terminal != null then
            terminal.close()