package model.map

import model.GameEntity
import model.Position2D
import model.MovableEntity
import model.Direction
import model.Wall
import model.GhostBasic
import model.Dot

/** It represents the game map with its entities. */
trait GameMap:
    val width: Int
    val height: Int

    /** Spacman spawnPoint */
    val spawnPoint: Position2D
    val ghostSpawnPoints: Set[Position2D]
    def getWalls: Set[Wall]
    def getGhosts: Set[GhostBasic]
    def getDots: Set[Dot]

    /** @param pos Position to check
      * @return Either an error message or the set of entities at that position
      */
    def entityAt(pos: Position2D): Either[String, Set[GameEntity]]

    /** @param entity Entity to place
      * @return Either an error message or the new map with the entity placed
      */
    def place(entity: GameEntity): Either[String, GameMap]

    /** @param entities Entities to place, polymorphic in type because of Scala's type inference limitations
      * @return Either an error message or the new map with the entities placed
      */
    def placeAll[E <: GameEntity](entities: Set[E]): Either[String, GameMap]

    /** @param entity Entity to replace
      * @param movedEntity New entity to place
      * @return Either an error message or the new map with the entity replaced
      */
    def replaceEntityTo(entity: GameEntity, movedEntity: GameEntity): Either[String, GameMap]

    /** @param entity Entity to remove
      * @return Either an error message or the new map with the entity removed
      */
    def remove(entity: GameEntity): Either[String, GameMap]

    /** @param entity Movable entity to check
      * @param dir Direction to move
      * @return true if the entity can move in the given direction, false otherwise
      */
    def canMove(entity: MovableEntity, dir: Direction): Boolean

case class GameMapImpl(
    width: Int,
    height: Int,
    spawnPoint: Position2D,
    private val ghostSpawnPointStartingCell: Position2D,
    grid: Map[Position2D, Set[GameEntity]]
) extends GameMap:

    /** Ghost spawn points are the 4 cells starting from the ghostSpawnPointStartingCell */
    val ghostSpawnPoints: Set[Position2D] = Set(
      ghostSpawnPointStartingCell,
      Position2D(ghostSpawnPointStartingCell.x + 1, ghostSpawnPointStartingCell.y),
      Position2D(ghostSpawnPointStartingCell.x, ghostSpawnPointStartingCell.y + 1),
      Position2D(ghostSpawnPointStartingCell.x + 1, ghostSpawnPointStartingCell.y + 1)
    )

    override def getWalls: Set[Wall] = grid.values.flatten.collect { case w: Wall => w }.toSet

    override def getGhosts: Set[GhostBasic] = grid.values.flatten.collect { case g: GhostBasic =>
        g
    }.toSet

    override def getDots: Set[Dot] = grid.values.flatten.collect { case d: Dot =>
        d
    }.toSet

    override def entityAt(pos: Position2D): Either[String, Set[GameEntity]] =
        grid.get(pos) match
            case Some(value) => Right(value)
            case None        => Left("Invalid position " + pos)

    override def place(entity: GameEntity): Either[String, GameMap] =
        grid.get(entity.position) match
            case Some(entities) =>
                Right(copy(grid = grid.updated(entity.position, entities + entity)))
            case None =>
                Left("Invalid position" + entity.position)

    override def placeAll[E <: GameEntity](entities: Set[E]): Either[String, GameMap] =
        entities.foldLeft[Either[String, GameMap]](Right(this)) { (result, entity) =>
            result.flatMap(_.place(entity))
        }

    override def remove(entity: GameEntity): Either[String, GameMap] =
        grid.get(entity.position) match
            case Some(entities) => entities.contains(entity) match
                    case true =>
                        Right(copy(grid = grid.updated(entity.position, entities - entity)))
                    case false => Left("No entity found")
            case None => Left("Invalid position" + entity.position)

    override def replaceEntityTo(
        entity: GameEntity,
        movedEntity: GameEntity
    ): Either[String, GameMap] =
        remove(entity) match
            case Right(map) => map.place(movedEntity)
            case Left(err)  => Left(err)

    override def canMove(entity: MovableEntity, dir: Direction): Boolean =
        val nextPos = entity.position.calculatePos(dir)
        entityAt(nextPos) match
            case Right(set) if set.exists(_.isInstanceOf[Wall]) => false
            case Right(set) if set.exists {
                    case _: GhostBasic => entity.isInstanceOf[GhostBasic]
                    case _             => false
                } => false
            case _ if isOutOfMap(nextPos) => false
            case _                        => true

    private def isOutOfMap(p: Position2D): Boolean =
        p.x >= width || p.x < 0 || p.y >= height || p.y < 0

/** Factory object for creating GameMap instances with the right grids but without entities. */
object GameMapFactory:
    def apply(
        width: Int,
        height: Int,
        spawnPoint: Position2D,
        ghostSpawnPoint: Position2D = Position2D(1, 1)
    ): GameMap =
        require(isSpawnPointInsideMap(width, height, spawnPoint))
        require(isSpawnPointInsideMap(width, height, ghostSpawnPoint))
        GameMapImpl(width, height, spawnPoint, ghostSpawnPoint, createEmptyMap(width, height))

    private def createEmptyMap(w: Int, h: Int): Map[Position2D, Set[GameEntity]] =
        (for
            x <- 0 until w
            y <- 0 until h
        yield Position2D(x, y) -> Set.empty[GameEntity]).toMap

    private def isSpawnPointInsideMap(width: Int, height: Int, spawnPoint: Position2D): Boolean =
        (spawnPoint.x >= 0 && spawnPoint.x <= width) && (spawnPoint.y >= 0 && spawnPoint.y <= height)
