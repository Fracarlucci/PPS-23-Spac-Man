package model

val DOT_BASIC_SCORE = 10
val DOT_FRUIT_SCORE = 50

trait GameEntity:
    def position: Position2D

trait MovableEntity extends GameEntity:

    def direction: Direction

    protected def withPosAndDir(newPosition: Position2D, newDirection: Direction): MovableEntity

    def move(newDirection: Direction): MovableEntity =
        val newPosition = position.calculatePos(newDirection)
        withPosAndDir(newPosition, newDirection)

    def teleport(destination: Position2D): MovableEntity = withPosAndDir(destination, direction)

case class Wall(position: Position2D) extends GameEntity
case class Tunnel(position: Position2D, toPos: Position2D, correctDirection: Direction)
    extends GameEntity:
    def canTeleport(dir: Direction): Boolean = dir == correctDirection

trait Dot extends GameEntity:
    val score: Int
case class DotBasic(position: Position2D, score: Int = DOT_BASIC_SCORE) extends Dot
case class DotFruit(position: Position2D, score: Int = DOT_FRUIT_SCORE) extends Dot
