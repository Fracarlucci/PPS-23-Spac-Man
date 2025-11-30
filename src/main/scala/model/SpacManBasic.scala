package model

case class SpacManBasic(val position: Position2D, val direction: Direction, val score: Int)
    extends MovableEntity:

    override def withPosAndDir(newPosition: Position2D, newDirection: Direction): SpacManBasic =
        this.copy(position = newPosition, direction = newDirection)

    def addScore(points: Int): SpacManBasic =
        if points < 0 then this
        else this.copy(score = this.score + points)

    def teleport(destination: Position2D): SpacManBasic =
        this.copy(position = destination)

    override def equals(obj: Any): Boolean = obj match
        case that: SpacManBasic =>
            this.position == that.position
        case _ => false
