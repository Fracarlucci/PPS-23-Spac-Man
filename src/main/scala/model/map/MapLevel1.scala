package model.map

import model.SpacManWithLife
import model.Position2D
import model.MapDSL
import model.GhostBasic
import model.Direction
import model.Tunnel
import model.DotPower
import model.DotFruit
import model.board
import model.genericWall
import model.DotBasic
import model.Wall

object MapLevel1 {
    def getMap(): (SpacManWithLife, GameMap) = createMap()

    private def createMap(): (SpacManWithLife, GameMap) =
        val ghostSpawnPoint = Position2D(1, 1)
        val spacmanSpawn    = Position2D(25, 17)

        val dsl = MapDSL(board(30, 20, spacmanSpawn, ghostSpawnPoint))

        val ghost1 = GhostBasic(Position2D(3, 3), Direction.Down, 1.0, 1)
        val ghost2 = GhostBasic(Position2D(25, 3), Direction.Up, 1.0, 2)
        val ghost3 = GhostBasic(Position2D(3, 17), Direction.Left, 1.0, 3)
        val ghost4 = GhostBasic(Position2D(25, 13), Direction.Right, 1.0, 4)

        val spacman = SpacManWithLife(spacmanSpawn, Direction.Left, 0)

        val tunnelL = Tunnel(Position2D(0, 12), Position2D(29, 12), Direction.Left)
        val tunnelR = Tunnel(Position2D(29, 12), Position2D(0, 12), Direction.Right)
        val dp1 = DotPower(Position2D(25, 18))
        val dp2 = DotPower(Position2D(2, 2))
        val fruit = DotFruit(Position2D(15, 12))

        import dsl.*

        // MURI ESTERNI
        place a genericWall() from position(0, 0)  to position(29, 0)
        place a genericWall() from position(0, 19) to position(29, 19)
        place a genericWall() from position(0, 0)  to position(0, 19)
        place a genericWall() from position(29, 0) to position(29, 19)

        // MURI INTERNI 
        // blocco centrale
        place a genericWall() from position(10, 8) to position(19, 8)
        place a genericWall() from position(10, 9) to position(10, 10)
        place a genericWall() from position(10, 13) to position(10, 15)
        place a genericWall() from position(19, 9) to position(19, 15)
        place a genericWall() from position(10, 15) to position(19, 15)

        // corridoio alto
        place a genericWall() from position(5, 4) to position(24, 4)

        // blocchi laterali
        place a genericWall() from position(3, 10) to position(5, 12)
        place a genericWall() from position(24, 10) to position(26, 12)

        // POSIZIONI MURI PER EVITARE DOT
        val wallPositions = dsl.map.getWalls.map(_.position)

        // PALLINI BASIC 
        for
            x <- 1 until 29
            y <- 1 until 19
            pos = Position2D(x, y)
            if (x + y) % 2 == 0 // piazza un dot ogni 2 caselle
            && !wallPositions.contains(pos)
            && pos != spacman.position
            && pos != dp1.position
            && pos != dp2.position
            && pos != fruit.position
            && !dsl.map.ghostSpawnPoints.contains(pos)
        do
            place a DotBasic(pos) at position(x, y)

        place a ghost1 at ghost1.position
        place a ghost2 at ghost2.position
        place a ghost3 at ghost3.position
        place a ghost4 at ghost4.position

        place a spacman at spacman.position

        place a tunnelL at position(0, 12)
        place a tunnelR at position(29, 12)

        place a dp1 at dp2.position
        place a dp2 at dp2.position

        place a fruit at fruit.position

        (spacman, dsl.map)
}
