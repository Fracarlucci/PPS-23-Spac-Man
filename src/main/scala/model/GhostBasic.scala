package model

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
