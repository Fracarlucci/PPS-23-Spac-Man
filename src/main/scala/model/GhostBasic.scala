package model

case class GhostBasic(val position: Position2D, val direction: Direction, val speed: Double, val id: Int) extends MovableEntity:
   
    override def withPosAndDir(newPosition: Position2D, newDirection: Direction): GhostBasic =
        this.copy(position = newPosition, direction = newDirection)

    def nextMove(): MovableEntity =
        val randomDir = Direction.values.toSeq(scala.util.Random.nextInt(Direction.values.size))
        this.move(randomDir)
