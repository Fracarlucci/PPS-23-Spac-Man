package model

val DOT_BASIC_SCORE = 10
val DOT_POWER_SCORE = 50
val DOT_FRUIT_SCORE = 100

trait GameEntity:
    def position: Position2D

trait MovableEntity extends GameEntity:

    def direction: Direction

    protected def withPosAndDir(newPosition: Position2D, newDirection: Direction): MovableEntity

    def move(newDirection: Direction): MovableEntity =
        val newPosition = position.calculatePos(newDirection)
        withPosAndDir(newPosition, newDirection)

    def teleport(destination: Position2D): MovableEntity = withPosAndDir(destination, direction)

sealed trait Dot extends GameEntity:
    def score: Int

case class DotBasic(position: Position2D) extends Dot:
    val score: Int = DOT_BASIC_SCORE

case class DotPower(position: Position2D) extends Dot:
    val score: Int = DOT_POWER_SCORE

case class DotFruit(position: Position2D) extends Dot:
    val score: Int = DOT_FRUIT_SCORE

case class Wall(position: Position2D) extends GameEntity

case class Tunnel(position: Position2D, toPos: Position2D, correctDirection: Direction) extends GameEntity:
    def canTeleport(dir: Direction): Boolean = dir == correctDirection

