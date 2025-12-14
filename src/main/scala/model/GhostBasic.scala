package model

import model.map.GameMap

val GHOST_BASIC_SCORE = 100

case class GhostBasic(
    position: Position2D,
    direction: Direction,
    id: Int,
    score: Int = GHOST_BASIC_SCORE
) extends MovableEntity:

    override def withPosAndDir(newPosition: Position2D, newDirection: Direction): GhostBasic =
        copy(position = newPosition, direction = newDirection)

    /**
     * Returns the next direction for the ghost.
     *
     * @param spacManPos the position of the SpacMan
     * @param spacManDir the direction of the SpacMan
     * @param gameMap the game map
     * @return the next direction for the ghost
     */
    def nextMove(
        spacManPos: Position2D,
        spacManDir: Direction,
        gameMap: GameMap
    ): Direction =
        val context = GhostContext(this, spacManPos, spacManDir, gameMap)
        GhostBehavior.forId(id).chooseDirection(context)
