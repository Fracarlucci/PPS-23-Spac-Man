package model.map

import model.GameEntity
import model.Position2D

trait GameMap:
  def width: Int
  def height: Int
  def entityAt(pos: Position2D): Either[String, Set[GameEntity]]
  def place(pos: Position2D, entity: GameEntity): Either[String, GameMap]

case class GameMapImpl(
  width: Int,
  height: Int,
  grid: Map[Position2D, Set[GameEntity]]
) extends GameMap:

  override def entityAt(pos: Position2D): Either[String, Set[GameEntity]] =
    grid.get(pos) match
      case Some(value) => Right(value)
      case None        => Left("Invalid position " + pos)  
    

  override def place(pos: Position2D, entity: GameEntity): Either[String, GameMap] =
    grid.get(pos) match
    case Some(entities) =>
      Right(copy(grid = grid.updated(pos, entities + entity)))
    case None =>
      Left("Invalid position" + pos)

object GameMapFactory:
  def apply(width: Int, height: Int): GameMap = GameMapImpl(width, height, createEmptyMap(width, height))

  private def createEmptyMap(w: Int, h: Int): Map[Position2D, Set[GameEntity]] = 
    (for
     x <- 0 until w
     y <- 0 until h
   yield Position2D(x, y) -> Set.empty[GameEntity]
  ).toMap


