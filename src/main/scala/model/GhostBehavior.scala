package model

import model.map.GameMap

/**
  * Context for ghost behavior.
  *
  * @param ghost the ghost
  * @param spacManPos the position of the SpacMan
  * @param spacManDir the direction of the SpacMan
  * @param gameMap the game map
  */
case class GhostContext(
    ghost: GhostBasic,
    spacManPos: Position2D,
    spacManDir: Direction,
    gameMap: GameMap
):
    /**
      * Returns true if the ghost can move in the given direction.
      */
    def canMove(direction: Direction): Boolean =
        gameMap.canMove(ghost, direction)

    /**
      * Returns the valid directions for the ghost to move.
      */
    def validDirections: Seq[Direction] =
        Direction.values.filter(canMove).toSeq

/**
  * Define the behavior of a ghost.
  */
sealed trait GhostBehavior:
    /**
      * Returns the direction for the ghost to move.
      */
    def chooseDirection(context: GhostContext): Direction

    /**
      * Returns the Manhattan distance between two positions.
      */
    protected final def manhattanDistance(pos1: Position2D, pos2: Position2D): Int =
        (pos1.x - pos2.x).abs + (pos1.y - pos2.y).abs

    /**
      * Returns the direction with the minimum or maximum Manhattan distance to the target position.
      */
    protected final def selectDirection(
        validDirs: Seq[Direction],
        ghostPos: Position2D,
        targetPos: Position2D,
        currentDir: Direction
    )(ordering: Ordering[Int]): Direction =
        validDirs
            .map(dir => dir -> manhattanDistance(ghostPos.calculatePos(dir), targetPos))
            .minByOption(_._2)(ordering)
            .map(_._1)
            .getOrElse(currentDir)

/**
  * Registry of ghost behaviors.
  */
object GhostBehavior:
    private val behaviorRegistry: Map[Int, GhostBehavior] = Map(
      1 -> ChaseBehavior,
      2 -> PredictiveBehavior,
      3 -> RandomBehavior,
      4 -> MixedBehavior
    )

    def forId(id: Int): GhostBehavior =
        behaviorRegistry.getOrElse(id, ChaseBehavior)

/**
  * Chase SpacMan.
  */
case object ChaseBehavior extends GhostBehavior:
    override def chooseDirection(context: GhostContext): Direction =
        selectDirection(
          context.validDirections,
          context.ghost.position,
          context.spacManPos,
          context.ghost.direction
        )(Ordering.Int)

/**
  * Predict SpacMan's future position and chase it.
  */
case object PredictiveBehavior extends GhostBehavior:
    private val PredictionDistance = 3

    private def predictTarget(context: GhostContext): Position2D =
        val offset = context.spacManDir match
            case Direction.Right => (PredictionDistance, 0)
            case Direction.Left  => (-PredictionDistance, 0)
            case Direction.Down  => (0, PredictionDistance)
            case Direction.Up    => (0, -PredictionDistance)
        Position2D(context.spacManPos.x + offset._1, context.spacManPos.y + offset._2)

    override def chooseDirection(context: GhostContext): Direction =
        selectDirection(
          context.validDirections,
          context.ghost.position,
          predictTarget(context),
          context.ghost.direction
        )(Ordering.Int)

/**
  * Move in a random direction when blocked.
  */
case object RandomBehavior extends GhostBehavior:
    override def chooseDirection(context: GhostContext): Direction =
        val canContinue = context.canMove(context.ghost.direction)

        if canContinue then
            context.ghost.direction
        else
            val validDirs = context.validDirections
            if validDirs.isEmpty then context.ghost.direction
            else validDirs(scala.util.Random.nextInt(validDirs.size))

/**
  * Mix between chase and predictive behavior based on distance.
  */
case object MixedBehavior extends GhostBehavior:
    private val DistanceThreshold = 3

    override def chooseDirection(context: GhostContext): Direction =
        val distance = manhattanDistance(context.ghost.position, context.spacManPos)
        val ordering = if distance > DistanceThreshold then Ordering.Int else Ordering.Int.reverse

        selectDirection(
          context.validDirections,
          context.ghost.position,
          context.spacManPos,
          context.ghost.direction
        )(ordering)
