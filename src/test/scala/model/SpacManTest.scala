package model

import org.scalatest.flatspec.AnyFlatSpec

class SpacManTest extends AnyFlatSpec:
    val startposition = Position2D(0, 0)
    val startDirection = Direction.Right
    val score = 0
    val spacMan = SpacManBasic(startposition, startDirection, score)

    it should "create a SpacManBasic" in:
        assert(spacMan.position == startposition && spacMan.direction == startDirection && spacMan.score == score)

    it should "add score correctly" in:
        val pointsToAdd = 10
        val newSpacMan = spacMan.addScore(pointsToAdd)
        assert(newSpacMan.score == score + pointsToAdd)

    it should "not remove score when adding negative points" in:
        val pointsToAdd = -10
        val newSpacMan = spacMan.addScore(pointsToAdd)
        assert(newSpacMan.score == score)
