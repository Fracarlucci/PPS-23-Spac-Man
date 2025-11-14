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
import model.GameEntity
import model.DotBasic
import model.RandomMovement
import model.GhostBuilder

class MapTest extends AnyFlatSpec with Matchers:

    val map = board(10, 10)

    "DSL" should "create the map" in:        
        val map = board(5, 5)

        map.width shouldBe 5
        map.height shouldBe 5

    it should "create the entity" in:
        val dsl = MapDSL(board(5, 5))
        val wall = Wall(Position2D(2, 1))
        val pacMan = SpacManBasic(Position2D(3, 1), Direction.Right, 0)
        val ghost = GhostBasic(Position2D(4, 1), Direction.Right, RandomMovement(), 1.0, 1)

        import dsl.*

        place a wall at position (2, 1)
        place a pacMan at position (3, 1)
        place a ghost at position (4, 1)

    "Map" should "get entity of a position" in:
        val dsl = MapDSL(map)
        val wall = Wall(Position2D(2, 1))
        val pacMan = SpacManBasic(Position2D(3, 1), Direction.Right, 0)
        val ghost = GhostBasic(Position2D(4, 1), Direction.Right, RandomMovement(), 1.0, 1)

        import dsl.*

        place a wall at position (2, 1)
        place a pacMan at position (4, 1)
        place a ghost at position (4, 1)

        dsl.map.entityAt(position(2, 1)) shouldBe Right(Set(wall))
        dsl.map.entityAt(position(4, 1)) shouldBe Right(Set(ghost, pacMan))

    it should "get an empty set" in:
        map.entityAt(Position2D(0, 0)) shouldBe Right(Set.empty[GameEntity])

    it should "catch an invalid position" in:
        map.entityAt(Position2D(11, 0)).isLeft shouldBe true
    
    it should "not place a game entity for invalid position" in:
        val wall = Wall(Position2D(-1, -1))
        val result = map.place(Position2D(-1, -1), wall)
        result.isLeft shouldBe true

    it should "return the set of walls" in:
        val dsl = MapDSL(map)
        val wall1 = Wall(Position2D(5, 1))
        val wall2 = Wall(Position2D(4, 1))
        val wall3 = Wall(Position2D(3, 1))

        import dsl.* 

        place a wall1 at position (5, 1)
        place a wall2 at position (4, 1)
        place a wall3 at position (3, 1)

        dsl.map.getWalls shouldBe Set(wall1, wall2, wall3) 

    
    it should "return the set of ghosts" in:
        val dsl = MapDSL(map)
        val ghost1 = GhostBuilder(Position2D(5, 1), Direction.Right, RandomMovement(), 1.0)
        val ghost2 = GhostBuilder(Position2D(4, 1), Direction.Right, RandomMovement(), 1.0)
        val ghost3 = GhostBuilder(Position2D(3, 1), Direction.Right, RandomMovement(), 1.0)

        import dsl.* 

        place a ghost1 at position (5, 1)
        place a ghost2 at position (4, 1)
        place a ghost3 at position (3, 1)

        dsl.map.getGhosts shouldBe Set(ghost1, ghost2, ghost3) 


    it should "return the set of dots" in:
        val dsl = MapDSL(map)
        val dot1 = DotBasic(Position2D(5, 1))
        val dot2 = DotBasic(Position2D(4, 1))
        val dot3 = DotBasic(Position2D(3, 1))

        import dsl.* 

        place a dot1 at position (5, 1)
        place a dot2 at position (4, 1)
        place a dot3 at position (3, 1)

        dsl.map.getDots shouldBe Set(dot1, dot2, dot3) 
    
    it should "return an empty sets" in:
        map.getWalls shouldBe Set.empty
        map.getGhosts shouldBe Set.empty
        map.getDots shouldBe Set.empty

    it should "return true calling canMove" in:
        val dsl = MapDSL(map)
        val wall = Wall(Position2D(2, 1))
        val pacMan = SpacManBasic(Position2D(3, 1), Direction.Right, 0)

        import dsl.*

        place a wall at position (2, 1)
        place a pacMan at position (3, 1)

        dsl.map.canMove(pacMan, Direction.Right) shouldBe true

    it should "return false calling canMove" in:
        val dsl = MapDSL(map)
        val wall = Wall(Position2D(2, 1))
        val pacMan = SpacManBasic(Position2D(3, 1), Direction.Right, 0)

        import dsl.*

        place a wall at position (2, 1)
        place a pacMan at position (3, 1)

        dsl.map.canMove(pacMan, Direction.Left) shouldBe false

    it should "return false calling canMove because pacman want to go out of map" in:
        val dsl = MapDSL(map)
        val wall = Wall(Position2D(2, 1))
        val pacMan = SpacManBasic(Position2D(0, 0), Direction.Right, 0)

        import dsl.*

        place a wall at position (2, 1)
        place a pacMan at position (0, 0)

        dsl.map.canMove(pacMan, Direction.Left) shouldBe false