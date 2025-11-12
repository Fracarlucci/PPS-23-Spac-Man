package model

import org.scalatest.flatspec.AnyFlatSpec

class GhostTest extends AnyFlatSpec:
    val startposition = Position2D(0, 0)
    val startDirection = Direction.Right
    val speed = 1.0
    val id = 1
    val ghost = GhostBasic(startposition, startDirection,speed, id)

    it should "create a ghost" in:
        assert(ghost.position == startposition && ghost.direction == startDirection && ghost.speed == speed && ghost.id == id)

    it should "change position" in:
        val newGhost = ghost.nextMove()
        assert(newGhost.position != startposition)
