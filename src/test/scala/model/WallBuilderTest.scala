package model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class WallBuilderTest extends AnyFlatSpec with Matchers:
  val startPosition = Position2D(0, 0)

  "WallBuilder" should "create an horizontal Walls" in:
    val endPos = Position2D(3, 0)
    val walls  = WallBuilder.createWalls(startPosition, endPos)
    val assertWalls = Set(
      Wall(Position2D(0, 0)),
      Wall(Position2D(1, 0)),
      Wall(Position2D(2, 0)),
      Wall(Position2D(3, 0))
    )

    walls shouldBe assertWalls

  it should "create an vertical Walls" in:
    val endPos = Position2D(0, 3)
    val walls  = WallBuilder.createWalls(startPosition, endPos)
    val assertWalls = Set(
      Wall(Position2D(0, 0)),
      Wall(Position2D(0, 1)),
      Wall(Position2D(0, 2)),
      Wall(Position2D(0, 3))
    )

    walls shouldBe assertWalls

  it should "create a single wall" in:
    val endPos      = Position2D(0, 0)
    val walls       = WallBuilder.createWalls(startPosition, endPos)
    val assertWalls = Set(Wall(Position2D(0, 0)))

    walls shouldBe assertWalls

  it should "create a square wall" in:
    val endPos = Position2D(2, 2)
    val walls  = WallBuilder.createWalls(startPosition, endPos)
    val assertWalls = Set(
      Wall(Position2D(0, 0)),
      Wall(Position2D(0, 1)),
      Wall(Position2D(0, 2)),
      Wall(Position2D(1, 0)),
      Wall(Position2D(2, 0)),
      Wall(Position2D(1, 1)),
      Wall(Position2D(2, 1)),
      Wall(Position2D(1, 2)),
      Wall(Position2D(2, 2))
    )

    walls shouldBe assertWalls

  it should "create a rectangle wall" in:
    val endPos = Position2D(3, 2)
    val walls  = WallBuilder.createWalls(startPosition, endPos)
    val assertWalls = Set(
      Wall(Position2D(0, 0)),
      Wall(Position2D(0, 1)),
      Wall(Position2D(0, 2)),
      Wall(Position2D(1, 0)),
      Wall(Position2D(2, 0)),
      Wall(Position2D(3, 0)),
      Wall(Position2D(1, 1)),
      Wall(Position2D(2, 1)),
      Wall(Position2D(3, 1)),
      Wall(Position2D(1, 2)),
      Wall(Position2D(2, 2)),
      Wall(Position2D(3, 2))
    )

    walls shouldBe assertWalls
