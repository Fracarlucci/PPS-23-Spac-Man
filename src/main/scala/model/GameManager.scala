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

    val collisionsManager: CollisionsManager = SimpleCollisionsManager(this)
    def getSpacMan: SpacManWithLife          = _spacMan

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

        movedGhosts.foreach(checkCollisionWithGhost)

    override def moveSpacMan(dir: Direction): Unit =
        if !_gameMap.canMove(_spacMan, dir) then
            return
        
        val movedSpacMan = _spacMan.move(dir).asInstanceOf[SpacManWithLife]
        
        _gameMap.replaceEntityTo(_spacMan, movedSpacMan) match
            case Right(updatedMap) =>
                _gameMap = updatedMap
                _spacMan = movedSpacMan
                checkCollisions(movedSpacMan, dir)
            case Left(error) =>
                println(s"Warning: Could not move SpacMan - $error")

    private def applyCollisionEffect(
        collision: CollisionType
    ): Unit =
        import CollisionType.*
        collision match
            case GhostCollision(ghost) =>
                handleGhostCollision(ghost)
                // if isChaseMode then Some(_spacMan) else None
            case DotBasicCollision(dot) =>
                _gameMap = _gameMap.remove(dot).getOrElse(_gameMap)
                _spacMan = _spacMan.addScore(dot.score)
            case DotPowerCollision(dot) =>
                _gameMap = _gameMap.remove(dot).getOrElse(_gameMap)
                _spacMan = _spacMan.addScore(dot.score)
                _chaseTimeRemaining = 10000
            case DotFruitCollision(fruit) =>
                _gameMap = _gameMap.remove(fruit).getOrElse(_gameMap)
                _spacMan = _spacMan.addScore(fruit.score).addLife()
            case TunnelCollision(tunnel) =>
                val teleportedSpacMan =
                    _spacMan.teleport(tunnel.toPos).asInstanceOf[SpacManWithLife]
                _gameMap.replaceEntityTo(_spacMan, teleportedSpacMan) match
                    case Right(updatedMap) =>
                        _gameMap = updatedMap
                        _spacMan = teleportedSpacMan
                    case Left(err) =>
                        println(s"Error teleporting SpacMan: $err")
            case NoCollision =>

    private def getRandomSpawnPosition(): Position2D =
        val spawnPoints = _gameMap.ghostSpawnPoints.toSeq
        val freeSpawnPoints = spawnPoints.filter { pos =>
            _gameMap.entityAt(pos).toOption
                .map(entities => !entities.exists(_.isInstanceOf[GhostBasic]))
                .getOrElse(true)
        }
        val availablePoints = if freeSpawnPoints.nonEmpty then freeSpawnPoints else spawnPoints
        availablePoints(scala.util.Random.nextInt(availablePoints.size))

    private def handleGhostCollision(ghost: GhostBasic): Unit =
        if isChaseMode then
            val randomSpawnPos  = getRandomSpawnPosition()
            val teleportedGhost = ghost.teleport(randomSpawnPos).asInstanceOf[GhostBasic]
            _gameMap.replaceEntityTo(ghost, teleportedGhost) match
                case Right(updatedMap) =>
                    _gameMap = updatedMap
                case Left(error) =>
                    println(s"Error teleporting ghost: $error")
        else if _spacMan.lives > 0 then
            _spacMan = _spacMan.removeLife()
            _gameOver = isSpacManDead()
            _gameOver match
                case false =>
                    val teleportedSpacMan = handleRemoveSpacManLife()
                    _gameMap =
                        _gameMap.replaceEntityTo(_spacMan, teleportedSpacMan).getOrElse(_gameMap)
                    _spacMan = teleportedSpacMan
                case true =>
                    _gameMap = _gameMap.remove(_spacMan).getOrElse(_gameMap)
        else
            _gameOver = true
            _gameMap = _gameMap.remove(_spacMan).getOrElse(_gameMap)

    private def isSpacManDead(): Boolean = _spacMan.lives == 0

    private def handleRemoveSpacManLife(): SpacManWithLife =
        _spacMan.teleport(_gameMap.spawnPoint).asInstanceOf[SpacManWithLife]

    private def checkCollisionWithGhost(ghost: GhostBasic): Unit =
        if ghost.position == _spacMan.position then
            handleGhostCollision(ghost)

    private def checkCollisions(
        spacMan: SpacManWithLife,
        direction: Direction
    ): Unit =
        _gameMap.entityAt(spacMan.position).toOption match
            case Some(entities) =>
                collisionsManager.detectCollision(entities, direction) match
                    case collision => applyCollisionEffect(collision)
            case None => ()
