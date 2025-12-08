package model

import model.{ChaseBehavior, GhostBehavior, PredictiveBehavior, RandomBehavior, MixedBehavior}
import model.map.GameMap

case class GhostBasic(
    position: Position2D,
    direction: Direction,
    speed: Double,
    id: Int
) extends MovableEntity:
  
  override def withPosAndDir(newPosition: Position2D, newDirection: Direction): GhostBasic =
    copy(position = newPosition, direction = newDirection)
  
  def nextMove(
      spacManPos: Position2D,
      spacManDir: Direction,
      gameMap: GameMap
  ): Direction =
    val context = GhostContext(this, spacManPos, spacManDir, gameMap)
    GhostBehavior.forId(id).chooseDirection(context)

case class GhostForTest(
    val position: Position2D,
    val direction: Direction,
    val speed: Double,
    val id: Int
) extends MovableEntity:

    override def withPosAndDir(newPosition: Position2D, newDirection: Direction): GhostForTest =
        this.copy(position = newPosition, direction = newDirection)

    def nextMove(
        canContinue: Boolean,
        spacManPos: Position2D,
        spacManDir: Direction,
        gameMap: map.GameMap
    ): Direction = Direction.Up
