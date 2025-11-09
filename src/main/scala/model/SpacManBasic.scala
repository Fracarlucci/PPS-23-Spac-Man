package model

case class SpacManBasic(val position: Position2D, val direction: Direction, val score: Int) extends MovableEntity:
    
    override def withPosAndDir(newPosition: Position2D, newDirection: Direction): SpacManBasic =
        this.copy(position = newPosition, direction = newDirection)

    def addScore(points: Int): SpacManBasic =
        if points < 0 then this
        else this.copy(score = this.score + points)
