package model

import model.map.GameMap
import model.map.GameMapImpl
import model.map.GameMapFactory

trait WallEntityBuilderLike:
  infix def to(endPos: Position2D): Unit

object board:
  def apply(
    width: Int,
    height: Int,
    spawnPoint: Position2D = Position2D(0, 0),
    ghostSpawnPoint: Position2D = Position2D(1, 1)
  ): GameMap = GameMapFactory.apply(width, height, spawnPoint, ghostSpawnPoint)

object genericWall:
  def apply(): Wall = Wall.apply(Position2D(0, 0))

class MapDSL(var map: GameMap):
  object place:
    def a(e: GameEntity): GameEntityBuilder = GameEntityBuilder(e)

  class GameEntityBuilder(e: GameEntity = null):
    infix def from(pos: Position2D): WallEntityBuilderLike = e match
      case Wall(_) => WallEntityBuilder(pos)
      case _         => NoWallFoundBuilder()

    infix def at(pos: Position2D): Unit =
      map = map.place(pos, e).fold(_ => map, identity)

  class WallEntityBuilder(startPos: Position2D) extends WallEntityBuilderLike:
    infix def to(endPos: Position2D): Unit =
      map = map.placeAll(WallBuilder.createWalls(startPos, endPos)).fold(_ => map, identity)

  class NoWallFoundBuilder() extends WallEntityBuilderLike:
    infix def to(pos: Position2D): Unit = ()

  /** DSL helper per scrivere at position (x, y) */
  object position:
    def apply(x: Int, y: Int): Position2D = Position2D(x, y)
