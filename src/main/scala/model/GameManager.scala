package model

import model.map.GameMap
import scala.annotation.tailrec
import model.CollisionsManager

trait GameManager:
    def getGameMap: GameMap
    def getSpacMan: SpacManWithLife
    def isWin(): Boolean
    def isGameOver(): Boolean
    def moveSpacMan(dir: Direction): Unit
    def moveGhosts(): Unit
    def isChaseMode: Boolean
    def updateChaseTime(deltaTime: Long): Unit

case class SimpleGameManager(
    private var _spacMan: SpacManWithLife,
    private var _gameMap: GameMap,
    private var _gameOver: Boolean = false,
    private var _chaseTimeRemaining: Long = 0
) extends GameManager:

    def getSpacMan: SpacManWithLife = _spacMan

    def getGameMap: GameMap = _gameMap

    override def isWin(): Boolean = _gameMap.getDots.isEmpty

    override def isGameOver(): Boolean = _gameOver

    override def isChaseMode: Boolean = _chaseTimeRemaining > 0

    override def updateChaseTime(deltaTime: Long): Unit =
        if _chaseTimeRemaining > 0 then
            _chaseTimeRemaining = Math.max(0, _chaseTimeRemaining - deltaTime)

    override def moveGhosts(): Unit =

        def attemptMove(ghost: GhostBasic): Option[GhostBasic] =
            val nextDirection = ghost.nextMove(
              _spacMan.position,
              _spacMan.direction,
              _gameMap
            )

            Option.when(_gameMap.canMove(ghost, nextDirection)) {
                ghost.move(nextDirection).asInstanceOf[GhostBasic]
            }

        def applyMove(ghost: GhostBasic, movedGhost: GhostBasic): GhostBasic =
            _gameMap.replaceEntityTo(ghost, movedGhost) match
                case Right(updatedMap) =>
                    _gameMap = updatedMap
                    movedGhost
                case Left(error) =>
                    println(s"Warning: Could not move ghost ${ghost.id} - $error")
                    ghost

        val movedGhosts = _gameMap.getGhosts.map { ghost =>
            attemptMove(ghost)
                .map(applyMove(ghost, _))
                .getOrElse(ghost)
        }

        movedGhosts.foreach { ghost =>
            CollisionsManager
                .checkGhostCollision(
                    ghost,
                    _spacMan,
                    _gameMap,
                    isChaseMode,
                    () => _gameOver = true
                )
                .foreach { (updatedMap, updatedSpacMan) =>
                    _gameMap = updatedMap
                    _spacMan = updatedSpacMan
                }
        }

    override def moveSpacMan(direction: Direction): Unit =
        if !_gameMap.canMove(_spacMan, direction) then
            return
        
        val movedSpacMan = _spacMan.move(direction).asInstanceOf[SpacManWithLife]
        
        _gameMap.replaceEntityTo(_spacMan, movedSpacMan) match
            case Right(updatedMap) =>
                _gameMap = updatedMap
                _spacMan = movedSpacMan
            case Left(error) =>
                println(s"Warning: Could not move SpacMan - $error")

        val entities =
            _gameMap.entityAt(movedSpacMan.position).toOption.getOrElse(Set.empty)

        val collision =
            CollisionsManager.detectCollision(entities, direction)

        CollisionsManager
            .applyCollisionEffect(
                collision,
                direction,
                _gameMap,
                _spacMan,
                isChaseMode,
                delta => _chaseTimeRemaining += delta,
                () => _gameOver = true
            )
            .map { (updatedMap, updatedSpacMan) =>
                _gameMap = updatedMap
                _spacMan = updatedSpacMan
                updatedSpacMan
            }
