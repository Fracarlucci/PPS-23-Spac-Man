package model

case class GhostBasic(
    val position: Position2D,
    val direction: Direction,
    val movementLogic: MovementLogic,
    val speed: Double,
    val id: Int
) extends MovableEntity:

  override def withPosAndDir(newPosition: Position2D, newDirection: Direction): GhostBasic =
    this.copy(position = newPosition, direction = newDirection)

  def nextMove(): MovableEntity =
    val randomDir = movementLogic.decide()
    this.move(randomDir)

object GhostBuilder:
  private var idCounter = 0
  
  def apply(position: Position2D, direction: Direction, movementLogic: MovementLogic, speed: Double): GhostBasic =
    idCounter = idCounter + 1
    GhostBasic(position, direction, movementLogic, speed, idCounter)

  def reset(): Unit = idCounter = 0
