package model.map

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import model.MapDSL
import model.Wall
import model.PacMan
import model.Ghost
import model.Position2D
import model.board

class MapTest extends AnyFlatSpec with Matchers:

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

        dsl.map.entityAt(position(2, 1)) shouldBe Some(Wall)
        dsl.map.entityAt(position(0, 1)).isEmpty shouldBe true
        dsl.map.entityAt(position(3, 1)) shouldBe Some(PacMan)
        dsl.map.entityAt(position(4, 1)) shouldBe Some(Ghost)