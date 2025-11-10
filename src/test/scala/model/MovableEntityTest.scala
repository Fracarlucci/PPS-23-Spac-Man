package model

class MovableEntityTest extends org.scalatest.flatspec.AnyFlatSpec:
    val startposition = Position2D(0, 0)
    val startDirection = Direction.Right
    val score = 0
    val entity = new SpacManBasic(startposition, startDirection, score)

    it should "move the entity up and change its direction" in:
        val movedEntity = entity.move(Direction.Up)
        assert(movedEntity.position == Position2D(0, -1))
        assert(movedEntity.direction == Direction.Up)

    it should "move the entity down and change its direction" in:
        val movedEntity = entity.move(Direction.Down)
        assert(movedEntity.position == Position2D(0, 1))
        assert(movedEntity.direction == Direction.Down)
        
    it should "move the entity left and change its direction" in:
        val movedEntity = entity.move(Direction.Left)
        assert(movedEntity.position == Position2D(-1, 0))
        assert(movedEntity.direction == Direction.Left)

    it should "move the entity right and change its direction" in:
        val movedEntity = entity.move(Direction.Right)
        assert(movedEntity.position == Position2D(1, 0))
        assert(movedEntity.direction == Direction.Right)
