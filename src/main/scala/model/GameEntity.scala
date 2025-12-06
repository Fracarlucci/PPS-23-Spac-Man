package model

val DOT_BASIC_SCORE = 10
val DOT_POWER_SCORE = 50

trait GameEntity:
    def position: Position2D

trait MovableEntity extends GameEntity:

    def direction: Direction

    protected def withPosAndDir(newPosition: Position2D, newDirection: Direction): MovableEntity

    def move(newDirection: Direction): MovableEntity =
        val newPosition = newDirection match
            case Direction.Up    => Position2D(position.x, position.y - 1)
            case Direction.Down  => Position2D(position.x, position.y + 1)
            case Direction.Left  => Position2D(position.x - 1, position.y)
            case Direction.Right => Position2D(position.x + 1, position.y)
        withPosAndDir(newPosition, newDirection)

    def teleport(destination: Position2D): MovableEntity = withPosAndDir(destination, direction)

sealed trait Dot extends GameEntity:
    def score: Int

case class DotBasic(position: Position2D) extends Dot:
    val score: Int = DOT_BASIC_SCORE

case class DotPower(position: Position2D) extends Dot:
    val score: Int = DOT_POWER_SCORE

case class Wall(position: Position2D) extends GameEntity

case class Tunnel(position: Position2D, toPos: Position2D, correctDirection: Direction) extends GameEntity:
    def canTeleport(dir: Direction): Boolean = dir == correctDirection
