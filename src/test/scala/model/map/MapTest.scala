package model.map

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import model.MapDSL
import model.Wall
import model.Position2D
import model.board
import model.GameEntity

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

        import dsl.*

        place a Wall at position (2, 1)
        place a PacMan at position (4, 1)
        place a Ghost at position (4, 1)

        dsl.map.entityAt(position(2, 1)) shouldBe Right(Set(Wall))
        dsl.map.entityAt(position(4, 1)) shouldBe Right(Set(GhostBasic, SpacManBasic))

    it should "get an empty set" in:
        map.entityAt(Position2D(0, 0)) shouldBe Right(Set.empty[GameEntity])

    it should "catch an invalid position" in:
        map.entityAt(Position2D(11, 0)).isLeft shouldBe true
    
    it should "not place a game entity for invalid position" in:
        val wall = Wall(Position2D(-1, -1))
        val result = map.place(Position2D(-1, -1), wall)
        result.isLeft shouldBe true
