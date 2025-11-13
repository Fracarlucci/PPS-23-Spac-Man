package model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GhostTest extends AnyFlatSpec with Matchers:
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

    "GhostBuilder" should "create two ghost with different IDs" in:
        val g1 = GhostBuilder(Position2D(0, 0), Direction.Up, 1.0)
        val g2 = GhostBuilder(Position2D(1, 1), Direction.Down, 1.0)

        g1.id shouldBe 1
        g2.id shouldBe 2

    it should "reset IDs" in:
        val g1 = GhostBuilder(Position2D(0, 0), Direction.Up, 1.0)
        val g2 = GhostBuilder(Position2D(1, 1), Direction.Down, 1.0)
        GhostBuilder.reset()
        val g3 = GhostBuilder(Position2D(1, 1), Direction.Down, 1.0)
        
        g1.id shouldBe 1
        g2.id shouldBe 2
        g3.id shouldBe 1
