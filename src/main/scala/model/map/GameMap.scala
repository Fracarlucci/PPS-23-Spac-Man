package model.map

import model.GameEntity
import model.Position2D

trait GameMap:
  def width: Int
  def height: Int
  def entityAt(pos: Position2D): Option[GameEntity]
  def place(pos: Position2D, entity: GameEntity): GameMap

case class GameMapImpl(
  width: Int,
  height: Int,
  grid: Map[Position2D, GameEntity] = Map.empty
) extends GameMap:

  override def entityAt(pos: Position2D): Option[GameEntity] =
    grid.get(pos)

  override def place(pos: Position2D, entity: GameEntity): GameMap =
    copy(grid = grid + (pos -> entity))