package model

import org.scalatest.flatspec.AnyFlatSpec

class Position2DTest extends AnyFlatSpec:
    val startPosition = Position2D(0, 0)

    it should "create a Position" in:
        val x = 2
        val y = 3
        val pos = Position2D(x, y)
        assert(pos.x == x && pos.y == y)

    it should "go up" in:
        val newPos = startPosition.calculatePos(Direction.Up)
        assert(newPos.equals(Position2D(0, 1)))

    it should "go down" in:
        val newPos = startPosition.calculatePos(Direction.Down)
        assert(newPos.equals(Position2D(0, -1)))

    it should "go left" in:
        val newPos = startPosition.calculatePos(Direction.Left)
        assert(newPos.equals(Position2D(-1, 0)))

    it should "go right" in:
        val newPos = startPosition.calculatePos(Direction.Right)
        assert(newPos.equals(Position2D(1, 0)))
