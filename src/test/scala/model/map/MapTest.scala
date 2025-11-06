package model.map

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import model.MapDSL
import model.Wall
import model.PacMan
import model.Ghost
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

        import dsl.*

        place a Wall at position (2, 1)
        place a PacMan at position (3, 1)
        place a Ghost at position (4, 1)

    it should "get entity of a position" in:
        val dsl = MapDSL(map)

        import dsl.*

        place a Wall at position (2, 1)
        place a PacMan at position (4, 1)
        place a Ghost at position (4, 1)

        dsl.map.entityAt(position(2, 1)) shouldBe Right(Set(Wall))
        dsl.map.entityAt(position(4, 1)) shouldBe Right(Set(Ghost, PacMan))

    it should "get an empty set" in:
        map.entityAt(Position2D(0, 0)) shouldBe Right(Set.empty[GameEntity])

    it should "catch an invalid position" in:
        map.entityAt(Position2D(11, 0)).isLeft shouldBe true
    
    it should "not place a game entity for invalid position" in:
        val result = map.place(Position2D(-1, -1), Wall)
        result.isLeft shouldBe true