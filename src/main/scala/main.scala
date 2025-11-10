import org.jline.terminal.TerminalBuilder
import org.jline.utils.NonBlockingReader

import model.{Direction, MovableEntity, SpacManBasic, Position2D}

@main def runJLine(): Unit =
    val terminal = TerminalBuilder.builder().system(true).build()
    val reader: NonBlockingReader = terminal.reader()

    var player: MovableEntity = SpacManBasic(Position2D(0, 0), Direction.Right, 0)
    var running = true

    println("Use W/A/S/D to move, Q to quit")

    while running do
        val ch = reader.read(100)
        if ch != -1 then
            ch.toChar.toLower match
                case 'w' => player = player.move(Direction.Up)
                case 'a' => player = player.move(Direction.Left)
                case 's' => player = player.move(Direction.Down)
                case 'd' => player = player.move(Direction.Right)
                case 'q' => running = false
                case _ => ()
            print(s"\rDirection: ${player.direction}, Position: ${player.position}")
            System.out.flush()

    reader.close()
    terminal.close()
    println("\nExit")