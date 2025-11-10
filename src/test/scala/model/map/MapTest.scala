package model.map

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import model.MapDSL
import model.Wall
import model.Position2D
import model.board
import org.scalactic.anyvals.PosInt
import model.Position2DTest
import model.SpacManBasic
import model.GhostBasic
import model.Direction

class MapTest extends AnyFlatSpec with Matchers:

    val map = board(10, 10)

    "DSL" should "create the map" in:        
        val map = board(5, 5)

        map.width shouldBe 5
        map.height shouldBe 5

    "DSL" should "create the entity" in:
        val dsl = MapDSL(board(5, 5))
        val wall = Wall(Position2D(2, 1))
        val pacMan = SpacManBasic(Position2D(3, 1), Direction.Right, 0)
        val ghost = GhostBasic(Position2D(4, 1), Direction.Right, 1.0, 1)

        import dsl.*

        place a wall at position (2, 1)
        place a pacMan at position (3, 1)
        place a ghost at position (4, 1)

    it should "get entity of a position" in:
        val dsl = MapDSL(map)
        val wall = Wall(Position2D(2, 1))
        val pacMan = SpacManBasic(Position2D(3, 1), Direction.Right, 0)
        val ghost = GhostBasic(Position2D(4, 1), Direction.Right, 1.0, 1)

        import dsl.*

        place a wall at position (2, 1)
        place a pacMan at position (4, 1)
        place a ghost at position (4, 1)

        dsl.map.entityAt(position(2, 1)) shouldBe Right(Set(Wall))
        dsl.map.entityAt(position(4, 1)) shouldBe Right(Set(Ghost, PacMan))

    it should "get an empty set" in:
        map.entityAt(Position2D(0, 0)) shouldBe Right(Set.empty[GameEntity])

    it should "catch an invalid position" in:
        map.entityAt(Position2D(11, 0)).isLeft shouldBe true
    
    it should "not place a game entity for invalid position" in:
        val result = map.place(Position2D(-1, -1), Wall)
        result.isLeft shouldBe true
        dsl.map.entityAt(position(2, 1)) shouldBe Some(wall)
        dsl.map.entityAt(position(0, 1)).isEmpty shouldBe true
        dsl.map.entityAt(position(3, 1)) shouldBe Some(pacMan)
        dsl.map.entityAt(position(4, 1)) shouldBe Some(ghost)
