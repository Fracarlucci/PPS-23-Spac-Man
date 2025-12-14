package model

import model.map.GameMap
import model.map.GameMapFactory

/** DSL builder trait for walls */
trait WallEntityBuilderLike:
    infix def to(endPos: Position2D): Unit

/** Generic entity types for DSL */
enum GenericEntity:
    case GenericWall
    case GenericDot
    case GenericDotPower
    case GenericDotFruit

/** DSL helper for creating empty game boards */
object board:
    def apply(
        width: Int,
        height: Int,
        spawnPoint: Position2D = Position2D(0, 0),
        ghostSpawnPoint: Position2D = Position2D(1, 1)
    ): GameMap = GameMapFactory.apply(width, height, spawnPoint, ghostSpawnPoint)

/** DSL for building and modifying GameMap instances */
class MapDSL(var map: GameMap):
    /** Val is created for writing codes 'human-like' */
    val genericWall     = GenericEntity.GenericWall
    val genericDot      = GenericEntity.GenericDot
    val genericDotPower = GenericEntity.GenericDotPower
    val genericDotFruit = GenericEntity.GenericDotFruit

    object place:
        /** DSL helper for writing 'a entity from/to position' or 'the entity at position'
         * useful with generic entities 
         * @param e Generic entity to place
        */
        def a(e: GenericEntity): GameEntityBuilder = GameEntityBuilder(e)
        /** DSL helper for writing 'the entity at position'
         * useful with already created entities 
         * @param e Game entity to place
        */
        def the(e: GameEntity): Unit =
            map = map.place(e).fold(_ => map, identity)
        /** DSL helper for writing 'multiple entities' 
         * @param gameEntitySet Set of game entities to place
        */
        def multiple[E <: GameEntity](gameEntitySet: Set[E]): Unit =
            map = map.placeAll(gameEntitySet).fold(_ => map, identity)

    class GameEntityBuilder(e: GenericEntity = null):
        /**
         *  DSL helper for writing 'from position' for walls 
         * @param pos Position where the wall starts
         * @return WallEntityBuilderLike for chaining 'to position'
        */
        infix def from(pos: Position2D): WallEntityBuilderLike = createGenericEntity(e) match
            case Wall(_) => WallEntityBuilder(pos)
            case _       => NoWallFoundBuilder()

        /** 
         * DSL helper for writing 'at position' 
         * @param pos Position where to place the entity
         * @return The created GameEntity
        */
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
        /** 
         * DSL helper for writing 'to position' for walls 
         * @param endPos Position where the wall ends
        */
        infix def to(endPos: Position2D): Unit =
            map = map.placeAll(WallBuilder.createWalls(startPos, endPos)).fold(_ => map, identity)

    class NoWallFoundBuilder() extends WallEntityBuilderLike:
        /** 
         * DSL helper for enabling write 'to position' when no wall is found
         * No exception is thrown, but nothing is done
         * @param pos 
         * @return Nothing
        */
        infix def to(pos: Position2D): Unit = ()

    /** DSL helper for writing 'position(x, y)' */
    object position:
        def apply(x: Int, y: Int): Position2D = Position2D(x, y)
