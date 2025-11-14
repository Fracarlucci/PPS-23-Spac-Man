package model

trait MovementLogic:
    def decide(): Direction

class RandomMovement() extends MovementLogic:
    def decide(): Direction = Direction.values.toSeq(scala.util.Random.nextInt(Direction.values.size))