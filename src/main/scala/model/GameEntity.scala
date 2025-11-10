package model

trait GameEntity:
    def position: Position2D

trait MovableEntity extends GameEntity:

    def direction: Direction

    def withPosAndDir(newPosition: Position2D, newDirection: Direction): MovableEntity

    def move(newDirection: Direction): MovableEntity =
        val newPosition = newDirection match
            case Direction.Up    => Position2D(position.x, position.y - 1)
            case Direction.Down  => Position2D(position.x, position.y + 1)
            case Direction.Left  => Position2D(position.x - 1, position.y)
            case Direction.Right => Position2D(position.x + 1, position.y)
        withPosAndDir(newPosition, newDirection)

case class Wall(position: Position2D) extends GameEntity
case class DotBasic(position: Position2D) extends GameEntity
