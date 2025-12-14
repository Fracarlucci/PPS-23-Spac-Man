package model

import model.map.GameMap
import model.map.GameMapFactory

trait WallEntityBuilderLike:
    infix def to(endPos: Position2D): Unit

enum GenericEntity:
    case GenericWall
    case GenericDot
    case GenericDotPower
    case GenericDotFruit

object board:
    def apply(
        width: Int,
        height: Int,
        spawnPoint: Position2D = Position2D(0, 0),
        ghostSpawnPoint: Position2D = Position2D(1, 1)
    ): GameMap = GameMapFactory.apply(width, height, spawnPoint, ghostSpawnPoint)

class MapDSL(var map: GameMap):
    val genericWall     = GenericEntity.GenericWall
    val genericDot      = GenericEntity.GenericDot
    val GenericDotPower = GenericEntity.GenericDotPower
    val GenericDotFruit = GenericEntity.GenericDotFruit

    object place:
        def a(e: GenericEntity): GameEntityBuilder = GameEntityBuilder(e)
        def the(e: GameEntity): Unit =
            map = map.place(e).fold(_ => map, identity)
        def multiple[E <: GameEntity](gameEntitySet: Set[E]): Unit =
            map = map.placeAll(gameEntitySet).fold(_ => map, identity)

    class GameEntityBuilder(e: GenericEntity = null):
        infix def from(pos: Position2D): WallEntityBuilderLike = createGenericEntity(e) match
            case Wall(_) => WallEntityBuilder(pos)
            case _       => NoWallFoundBuilder()

        infix def at(pos: Position2D): GameEntity =
            val gameEntity = createGenericEntity(e, pos)
            map = map.place(gameEntity).fold(_ => map, identity)
            gameEntity

        private def createGenericEntity(
            e: GenericEntity,
            pos: Position2D = Position2D(0, 0)
        ): GameEntity = e match
            case GenericEntity.GenericWall     => Wall(pos)
            case GenericEntity.GenericDot      => DotBasic(pos)
            case GenericEntity.GenericDotPower => DotPower(pos)
            case GenericEntity.GenericDotFruit => DotFruit(pos)

    class WallEntityBuilder(startPos: Position2D) extends WallEntityBuilderLike:
        infix def to(endPos: Position2D): Unit =
            map = map.placeAll(WallBuilder.createWalls(startPos, endPos)).fold(_ => map, identity)

    class NoWallFoundBuilder() extends WallEntityBuilderLike:
        infix def to(pos: Position2D): Unit = ()

    /** DSL helper per scrivere at position (x, y) */
    object position:
        def apply(x: Int, y: Int): Position2D = Position2D(x, y)
