import org.jline.terminal.TerminalBuilder
import org.jline.utils.NonBlockingReader

import model.{Direction, MovableEntity, SpacManBasic, Position2D}
import model.map.GameMap
import model.board

@main def runJLine(): Unit =
    val terminal = TerminalBuilder.builder().system(true).build()
    val reader: NonBlockingReader = terminal.reader()

    val map: GameMap = board(10, 10)

    var player: MovableEntity = SpacManBasic(Position2D(0, 0), Direction.Right, 0)
    var running = true

    map.place(player.position, player)

    println("Use W/A/S/D to move, Q to quit")

    while running do
        val ch = reader.read(100)
        if ch != -1 then
            ch.toChar.toLower match
                case 'w' => {
                        require(map.canMove(player, Direction.Up))
                        player = player.move(Direction.Up)
                        }
                case 'a' => {
                        require(map.canMove(player, Direction.Left))
                        player = player.move(Direction.Left)
                        }
                case 's' => {
                        require(map.canMove(player, Direction.Down))
                        player = player.move(Direction.Down)
                        }
                case 'd' => {
                        require(map.canMove(player, Direction.Right))
                        player = player.move(Direction.Right)
                        }
                case 'q' => running = false
                case _ => ()
            print(s"\rDirection: ${player.direction}, Position: ${player.position}")
            System.out.flush()

    reader.close()
    terminal.close()
    println("\nExit")