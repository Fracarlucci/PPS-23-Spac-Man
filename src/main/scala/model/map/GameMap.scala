package model.map

import model.GameEntity
import model.Position2D
import model.MovableEntity
import model.Direction
import model.Wall
import model.GhostBasic
import model.DotBasic

trait GameMap:
    def width: Int
    def height: Int
    def getWalls: Set[Wall]
    def getGhosts: Set[GhostBasic]
    def getDots: Set[DotBasic]
    def entityAt(pos: Position2D): Either[String, Set[GameEntity]]
    def place(pos: Position2D, entity: GameEntity): Either[String, GameMap]
    def placeAll[E <: GameEntity](entities: Set[E]): Either[String, GameMap]
    def remove(entity: GameEntity): Either[String, GameMap]
    def replaceEntityTo(entity: GameEntity, nextPos: Position2D): Either[String, GameMap]
    def canMove(entity: MovableEntity, dir: Direction): Boolean

case class GameMapImpl(
    width: Int,
    height: Int,
    grid: Map[Position2D, Set[GameEntity]]
) extends GameMap:

    override def getWalls: Set[Wall] = grid.values.flatten.collect { case w: Wall => w }.toSet

    override def getGhosts: Set[GhostBasic] = grid.values.flatten.collect { case g: GhostBasic =>
        g
    }.toSet

    override def getDots: Set[DotBasic] = grid.values.flatten.collect { case d: DotBasic =>
        d
    }.toSet

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

    override def placeAll[E <: GameEntity](entities: Set[E]): Either[String, GameMap] =
        entities.foldLeft[Either[String, GameMap]](Right(this)) { (result, entity) =>
            result.flatMap(_.place(entity.position, entity))
        }

    override def remove(entity: GameEntity): Either[String, GameMap] =
        grid.get(entity.position) match
            case Some(entities) => entities.contains(entity) match
                    case true =>
                        Right(copy(grid = grid.updated(entity.position, entities - entity)))
                    case false => Left("No entity found")
            case None => Left("Invalid position" + entity.position)

    override def replaceEntityTo(entity: GameEntity, nextPos: Position2D): Either[String, GameMap] =
        remove(entity) match
            case Right(map) => place(nextPos, entity)
            case Left(err)  => Left(err)

    override def canMove(entity: MovableEntity, dir: Direction): Boolean =
        val nextPos = entity.position.calculatePos(dir)
        entityAt(nextPos) match
            case Right(set) if set.exists {
                    case _: Wall       => true
                    case _: GhostBasic => true
                } => false
            case _ if isOutOfMap(nextPos) => false
            case _                        => true

    private def isOutOfMap(p: Position2D): Boolean =
        (p.x > width || p.x < 0) || (p.y > height || p.y < 0)

object GameMapFactory:
    def apply(width: Int, height: Int): GameMap =
        GameMapImpl(width, height, createEmptyMap(width, height))

    private def createEmptyMap(w: Int, h: Int): Map[Position2D, Set[GameEntity]] =
        (for
            x <- 0 until w
            y <- 0 until h
        yield Position2D(x, y) -> Set.empty[GameEntity]).toMap
