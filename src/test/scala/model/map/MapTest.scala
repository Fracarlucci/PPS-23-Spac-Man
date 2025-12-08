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
import model.WallBuilder

class MapTest extends AnyFlatSpec with Matchers:

    val map = board(10, 10)

    "DSL" should "create the map" in:
        val map = board(5, 5)

        map.width shouldBe 5
        map.height shouldBe 5

    it should "create the entity" in:
        val dsl    = MapDSL(board(5, 5))
        val wall   = Wall(Position2D(2, 1))
        val pacMan = SpacManBasic(Position2D(3, 1), Direction.Right, 0)
        val ghost  = GhostBasic(Position2D(4, 1), Direction.Right, 1.0, 1)

        import dsl.*

        place the wall
        place the pacMan
        place the ghost

    it should "create and place a set of Wall" in:
        val dsl = MapDSL(map)

        import dsl.*

        place a genericWall from position(0, 0) to position(0, 5)

        dsl.map.getWalls shouldBe (WallBuilder.createWalls(position(0, 0), position(0, 5)))

    it should "create nothing because entities are not walls" in:
        val dsl = MapDSL(map)
        val dot = DotBasic(Position2D(0, 0))
        import dsl.*

        place a genericDot from position(0, 0) to position(0, 5)

        dsl.map.getDots shouldBe Set.empty

    "Map" should "get entity of a position" in:
        val dsl    = MapDSL(map)
        val wall   = Wall(Position2D(2, 1))
        val pacMan = SpacManBasic(Position2D(4, 1), Direction.Right, 0)
        val ghost  = GhostBasic(Position2D(4, 1), Direction.Right, 1.0, 1)

        import dsl.*

        place the wall
        place the pacMan
        place the ghost

        dsl.map.entityAt(position(2, 1)) shouldBe Right(Set(wall))
        dsl.map.entityAt(position(4, 1)) shouldBe Right(Set(ghost, pacMan))

    it should "get an empty set" in:
        map.entityAt(Position2D(0, 0)) shouldBe Right(Set.empty[GameEntity])

    it should "catch an invalid position" in:
        map.entityAt(Position2D(11, 0)).isLeft shouldBe true

    it should "not place a game entity for invalid position" in:
        val wall   = Wall(Position2D(-1, -1))
        val result = map.place(Position2D(-1, -1), wall)
        result.isLeft shouldBe true

    it should "place all the entities in the map" in:
        val walls  = WallBuilder.createWalls(Position2D(0, 0), Position2D(0, 3))
        val result = map.placeAll(walls)

        result match
            case Right(map)   => map.getWalls shouldBe walls
            case Left(errMsg) => fail(errMsg)

    it should "fail while placing all the entities in the map" in:
        val walls  = WallBuilder.createWalls(Position2D(0, -2), Position2D(0, 3))
        val result = map.placeAll(walls)

        result match
            case Right(map)   => fail("The map should fail")
            case Left(errMsg) => errMsg shouldBe "Invalid position" + Position2D(0, -2)

    it should "return the set of walls" in:
        val dsl   = MapDSL(map)
        val wall1 = Wall(Position2D(5, 1))
        val wall2 = Wall(Position2D(4, 1))
        val wall3 = Wall(Position2D(3, 1))
        val walls = Set(wall1, wall2, wall3)

        import dsl.*

        place multiple walls

        dsl.map.getWalls shouldBe Set(wall1, wall2, wall3)

    it should "return the set of ghosts" in:
        val dsl    = MapDSL(map)
        val ghost1 = GhostBasic(Position2D(5, 1), Direction.Right, 1.0, 1)
        val ghost2 = GhostBasic(Position2D(4, 1), Direction.Right, 1.0, 1)
        val ghost3 = GhostBasic(Position2D(3, 1), Direction.Right, 1.0, 1)
        val ghosts = Set(ghost1, ghost2, ghost3)

        import dsl.*

        place multiple ghosts

        dsl.map.getGhosts shouldBe Set(ghost1, ghost2, ghost3)

    it should "return the set of dots" in:
        val dsl  = MapDSL(map)
        val dot1 = DotBasic(Position2D(5, 1))
        val dot2 = DotBasic(Position2D(4, 1))
        val dot3 = DotBasic(Position2D(3, 1))
        val dots = Set(dot1, dot2, dot3)

        import dsl.*

        place multiple dots

        dsl.map.getDots shouldBe Set(dot1, dot2, dot3)

    it should "return an empty sets" in:
        map.getWalls shouldBe Set.empty
        map.getGhosts shouldBe Set.empty
        map.getDots shouldBe Set.empty

    it should "return true calling canMove" in:
        val dsl    = MapDSL(map)
        val wall   = Wall(Position2D(2, 1))
        val pacMan = SpacManBasic(Position2D(3, 1), Direction.Right, 0)

        import dsl.*

        place the wall
        place the pacMan

        dsl.map.canMove(pacMan, Direction.Right) shouldBe true

    it should "return false calling canMove" in:
        val dsl    = MapDSL(map)
        val wall   = Wall(Position2D(2, 1))
        val pacMan = SpacManBasic(Position2D(3, 1), Direction.Right, 0)

        import dsl.*

        place the wall
        place the pacMan

        dsl.map.canMove(pacMan, Direction.Left) shouldBe false

    it should "return false calling canMove because pacman want to go out of map" in:
        val dsl    = MapDSL(map)
        val wall   = Wall(Position2D(2, 1))
        val pacMan = SpacManBasic(Position2D(0, 0), Direction.Right, 0)

        import dsl.*

        place the wall
        place the pacMan

        dsl.map.canMove(pacMan, Direction.Left) shouldBe false

    it should "remove the entity from the map" in:
        val dsl = MapDSL(map)

        import dsl.*

        place a genericWall from position(0, 0) to position(5, 0)
        dsl.map.remove(Wall(position(1, 0)))

    it should "return an invalid position" in:
        val dsl = MapDSL(map)

        import dsl.*

        place a genericWall from position(0, 0) to position(5, 0)
        dsl.map.remove(Wall(position(-1, 0))) shouldBe Left("Invalid position" + position(
          -1,
          0
        ))

    it should "return a not found entity" in:
        val dsl = MapDSL(map)

        import dsl.*

        place a genericWall from position(0, 0) to position(5, 0)
        dsl.map.remove(DotBasic(position(0, 0))) shouldBe Left("No entity found")

    it should "return replace the pacman to the new position" in:
        val dsl          = MapDSL(map)
        val pacMan       = SpacManBasic(Position2D(0, 0), Direction.Right, 0)
        val movedSpacMan = SpacManBasic(Position2D(2, 1), Direction.Right, 0)

        import dsl.*

        place the pacMan
        place a genericWall from position(0, 0) to position(5, 0)

        dsl.map.replaceEntityTo(pacMan, movedSpacMan).isRight shouldBe true

    it should "return an invalid position while trying to replace an entity" in:
        val dsl          = MapDSL(map)
        val pacMan       = SpacManBasic(Position2D(0, 0), Direction.Right, 0)
        val movedSpacMan = SpacManBasic(Position2D(-1, 0), Direction.Right, 0)

        import dsl.*

        place the pacMan
        place a genericWall from position(0, 0) to position(5, 0)

        dsl.map.replaceEntityTo(pacMan, movedSpacMan) shouldBe Left(
          "Invalid position" + position(-1, 0)
        )

    it should "have a default SpacMan spawnPoint" in:
        map.spawnPoint shouldBe Position2D(0, 0)

    it should "be possible to create a map with a SpacMan spawnPoint" in:
        val newMap = board(10, 10, Position2D(2, 2))
        newMap.spawnPoint shouldBe Position2D(2, 2)

    it should "throw and IllegalArgumentException because of SpacMan spawnPoint out of map" in:
        intercept[IllegalArgumentException]:
            val newMap = board(10, 10, Position2D(11, 11))

    it should "have a default Ghost spawnPoint" in:
        map.ghostSpawnPoints shouldBe Set(
          Position2D(1, 1),
          Position2D(2, 1),
          Position2D(1, 2),
          Position2D(2, 2)
        )

    it should "be possible to create a map with a Ghost spawnPoint" in:
        val newMap = board(10, 10, Position2D(2, 2), Position2D(3, 3))
        newMap.ghostSpawnPoints shouldBe Set(
          Position2D(3, 3),
          Position2D(4, 3),
          Position2D(3, 4),
          Position2D(4, 4)
        )

    it should "throw and IllegalArgumentException because of Ghost spawnPoint out of map" in:
        intercept[IllegalArgumentException]:
            val newMap = board(10, 10, Position2D(11, 11), Position2D(12, 12))
