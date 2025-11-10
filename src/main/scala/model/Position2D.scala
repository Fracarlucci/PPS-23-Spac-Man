package model

case class Position2D(x: Int, y: Int): 
    def calculatePos(dir: Direction): Position2D = dir match
        case Direction.Up    => Position2D(x, y + 1)
        case Direction.Down  => Position2D(x, y - 1)
        case Direction.Left  => Position2D(x - 1, y)
        case Direction.Right => Position2D(x + 1, y)
    