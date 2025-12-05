package model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SpacManTest extends AnyFlatSpec with Matchers:
    val startposition = Position2D(0, 0)
    val startDirection = Direction.Right
    val score = 0
    val spacMan = SpacManBasic(startposition, startDirection, score)

    it should "create a SpacManBasic" in:
        assert(spacMan.position == startposition && spacMan.direction == startDirection && spacMan.score == score)

    "SpacManBasic" should "add score correctly" in:
        val pointsToAdd = 10
        val newSpacMan = spacMan.addScore(pointsToAdd)
        assert(newSpacMan.score == score + pointsToAdd)

    it should "not remove score when adding negative points" in:
        val pointsToAdd = -10
        val newSpacMan = spacMan.addScore(pointsToAdd)
        assert(newSpacMan.score == score)

    val spacManWithLife = SpacManWithLife(startposition, startDirection, score)

    "SpacManWithLife" should "has a default life" in:
        spacManWithLife.lives shouldBe 3

    it should "add correctly a life" in:
        val updatedSpacMan = spacManWithLife.addLife()
        updatedSpacMan.lives shouldBe 4

    it should "remove correctly a life" in:
        val updatedSpacMan = spacManWithLife.removeLife()
        updatedSpacMan.lives shouldBe 2

