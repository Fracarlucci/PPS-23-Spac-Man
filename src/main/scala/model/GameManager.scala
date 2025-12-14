package model

import model.map.GameMap

/** Immutable game state */
case class GameState(
    spacMan: SpacManWithLife,
    gameMap: GameMap,
    gameOver: Boolean = false,
    chaseTimeRemaining: Long = 0
):
    def isWin: Boolean = gameMap.getDots.isEmpty

    def isChaseMode: Boolean = chaseTimeRemaining > 0

    def updateChaseTime(deltaTime: Long): GameState =
        if chaseTimeRemaining > 0 then
            copy(chaseTimeRemaining = Math.max(0, chaseTimeRemaining - deltaTime))
        else
            this

trait GameManager:
    def getState: GameState
    def isWin(): Boolean
    def isGameOver(): Boolean
    def isChaseMode: Boolean
    def moveSpacMan(dir: Direction): Unit
    def moveGhosts(): Unit
    def updateChaseTime(deltaTime: Long): Unit

/**
  * Game manager implementation.
  *
  * @param state The current game state.
  */
class SimpleGameManager(private var state: GameState) extends GameManager:

    def getState: GameState = state

    override def isWin(): Boolean = state.isWin

    override def isGameOver(): Boolean = state.gameOver

    override def isChaseMode: Boolean = state.isChaseMode

    /**
     * Updates the chase time remaining.
     */
    override def updateChaseTime(deltaTime: Long): Unit =
        state = state.updateChaseTime(deltaTime)

    /**
     * Moves all ghosts in the game.
     */
    override def moveGhosts(): Unit =

        /**
         * Attempts to move a ghost to the next position.
         */
        def attemptMove(
            ghost: GhostBasic,
            currentMap: GameMap,
            currentSpacMan: SpacManWithLife
        ): Option[GhostBasic] =
            val nextDirection = ghost.nextMove(
              currentSpacMan.position,
              currentSpacMan.direction,
              currentMap
            )
            Option.when(currentMap.canMove(ghost, nextDirection)):
                ghost.move(nextDirection).asInstanceOf[GhostBasic]

        /**
         * Applies a move to a ghost.
         */
        def applyMove(ghost: GhostBasic, movedGhost: GhostBasic, currentMap: GameMap): GameMap =
            currentMap.replaceEntityTo(ghost, movedGhost) match
                case Right(updatedMap) => updatedMap
                case Left(error) =>
                    println(s"Warning: Could not move ghost ${ghost.id} - $error")
                    currentMap

        /**
         * Moves all ghosts in the game.
         */
        val (updatedMap, movedGhosts) =
            state.gameMap.getGhosts.foldLeft((state.gameMap, List.empty[GhostBasic])):
                case ((currentMap, ghosts), ghost) =>
                    attemptMove(ghost, currentMap, state.spacMan) match
                        case Some(movedGhost) =>
                            val newMap = applyMove(ghost, movedGhost, currentMap)
                            (newMap, movedGhost :: ghosts)
                        case None =>
                            (currentMap, ghost :: ghosts)

        /**
         * Checks collisions for all moved ghosts.
         */
        val finalState =
            movedGhosts.foldLeft(state.copy(gameMap = updatedMap)): (currentState, ghost) =>
                CollisionsManager
                    .checkGhostCollision(
                      ghost,
                      currentState.spacMan,
                      currentState.gameMap,
                      currentState.isChaseMode,
                      () => {}
                    )
                    .map: (newMap, newSpacMan) =>
                        val gameOver = newSpacMan.lives <= 0
                        currentState.copy(
                          gameMap = newMap,
                          spacMan = newSpacMan,
                          gameOver = gameOver
                        )
                    .getOrElse(currentState)

        state = finalState

    /**
     * Moves the SpacMan in the given direction.
     */
    override def moveSpacMan(direction: Direction): Unit =
        if !state.gameMap.canMove(state.spacMan, direction) then
            return

        val movedSpacMan = state.spacMan.move(direction).asInstanceOf[SpacManWithLife]

        val updatedMapAfterMove = state.gameMap.replaceEntityTo(state.spacMan, movedSpacMan) match
            case Right(updatedMap) => updatedMap
            case Left(error) =>
                println(s"Warning: Could not move SpacMan - $error")
                return

        val entities = updatedMapAfterMove
            .entityAt(movedSpacMan.position)
            .toOption
            .getOrElse(Set.empty)

        val collision = CollisionsManager.detectCollision(entities, direction)

        var chaseTimeDelta = 0L
        var gameOverFlag   = false

        val finalState = CollisionsManager
            .applyCollisionEffect(
              collision,
              direction,
              updatedMapAfterMove,
              movedSpacMan,
              state.isChaseMode,
              delta => chaseTimeDelta += delta,
              () => gameOverFlag = true
            )
            .map: (finalMap, finalSpacMan) =>
                state.copy(
                  gameMap = finalMap,
                  spacMan = finalSpacMan,
                  chaseTimeRemaining = state.chaseTimeRemaining + chaseTimeDelta,
                  gameOver = gameOverFlag || finalSpacMan.lives <= 0
                )
            .getOrElse(state.copy(gameMap = updatedMapAfterMove, spacMan = movedSpacMan))

        state = finalState

/** Companion object for SimpleGameManager. */
object SimpleGameManager:
    def apply(
        spacMan: SpacManWithLife,
        gameMap: GameMap,
        gameOver: Boolean = false,
        chaseTimeRemaining: Long = 0
    ): SimpleGameManager =
        new SimpleGameManager(GameState(spacMan, gameMap, gameOver, chaseTimeRemaining))
