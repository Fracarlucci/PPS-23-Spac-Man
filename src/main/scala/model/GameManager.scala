package model

import model.map.GameMap
import scala.annotation.tailrec

trait GameManager:
    def getGameMap: GameMap
    def getSpacMan: SpacManWithLife
    def isWin(): Boolean
    def isGameOver(): Boolean
    def moveSpacManAndCheck(newDirection: Direction): Option[SpacManWithLife]
    def moveGhosts(): Set[GhostBasic]
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

    override def moveGhosts(): Set[GhostBasic] =
        @tailrec
        def findValidMove(ghost: GhostBasic, attempts: Int = 10): GhostBasic =
            if attempts <= 0 then ghost
            else
                val nextDirection = ghost.nextMove()
                if _gameMap.canMove(ghost, nextDirection) then
                    val movedGhost = ghost.move(nextDirection).asInstanceOf[GhostBasic]
                    _gameMap.replaceEntityTo(ghost, movedGhost) match
                        case Right(updatedMap) =>
                            _gameMap = updatedMap
                            movedGhost
                        case Left(error) =>
                            println(s"Error moving Ghost: $error")
                            ghost
                else
                    findValidMove(ghost, attempts - 1)

        val movedGhosts = _gameMap.getGhosts.map(findValidMove(_))
        movedGhosts.foreach(checkCollisionWithGhost)
        movedGhosts

    private def moveSpacMan(newDirection: Direction): SpacManWithLife =
        if _gameMap.canMove(_spacMan, newDirection) then
            val movedSpacMan = _spacMan.move(newDirection).asInstanceOf[SpacManWithLife]
            _gameMap.replaceEntityTo(_spacMan, movedSpacMan) match
                case Right(updatedMap) =>
                    _gameMap = updatedMap
                    _spacMan = movedSpacMan
                case Left(error) =>
                    println(s"Error moving SpacMan: $error")
        _spacMan

    private enum CollisionResult:
        case GhostCollision(ghost: GhostBasic)
        case DotBasicCollision(dot: DotBasic)
        case DotPowerCollision(dot: DotPower)
        case DotFruitCollision(fruit: DotFruit)
        case TunnelCollision(tunnel: Tunnel)
        case NoCollision

    private def detectCollision(
        entities: Set[GameEntity],
        direction: Direction
    ): CollisionResult =
        import CollisionResult.*
        entities.collectFirst { case ghost: GhostBasic =>
            GhostCollision(ghost)
        }.orElse(
            entities.collectFirst { case fruit: DotFruit =>
                DotFruitCollision(fruit)
            }
        ).orElse(
            entities.collectFirst { case dot: DotPower =>
                DotPowerCollision(dot)
            }
        ).orElse(
            entities.collectFirst { case dot: DotBasic =>
                DotBasicCollision(dot)
            }
        ).orElse(
            entities.collectFirst {
                case tunnel: Tunnel if tunnel.canTeleport(direction) =>
                  TunnelCollision(tunnel)
          }
        ).getOrElse(NoCollision)

    private def applyCollisionEffect(
        spacMan: SpacManWithLife,
        collision: CollisionResult
    ): Option[SpacManWithLife] =
        import CollisionResult.*
        collision match
            case GhostCollision(ghost) =>
                handleGhostCollision(ghost)
                if isChaseMode then Some(_spacMan) else None
            case DotBasicCollision(dot) =>
                _gameMap = _gameMap.remove(dot).getOrElse(_gameMap)
                _spacMan = _spacMan.addScore(dot.score)
                Some(_spacMan)
            case DotPowerCollision(dot) =>
                _gameMap = _gameMap.remove(dot).getOrElse(_gameMap)
                _spacMan = _spacMan.addScore(dot.score)
                _chaseTimeRemaining = 10000
                Some(_spacMan)
            case DotFruitCollision(fruit) =>
                _gameMap = _gameMap.remove(fruit).getOrElse(_gameMap)
                _spacMan = _spacMan.addScore(fruit.score).addLife()
                Some(_spacMan)
            case TunnelCollision(tunnel) =>
                val teleportedSpacMan = spacMan.teleport(tunnel.toPos).asInstanceOf[SpacManWithLife]
                _gameMap.replaceEntityTo(spacMan, teleportedSpacMan) match
                    case Right(updatedMap) =>
                        _gameMap = updatedMap
                        _spacMan = teleportedSpacMan
                        Some(_spacMan)
                    case Left(err) =>
                        println(s"Error teleporting SpacMan: $err")
                        None
            case NoCollision =>
                Some(_spacMan)

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
    ): Option[SpacManWithLife] =
        _gameMap.entityAt(spacMan.position).toOption
            .map(entities => detectCollision(entities, direction))
            .map(collision => applyCollisionEffect(spacMan, collision))
            .getOrElse(Some(_spacMan))

    def moveSpacManAndCheck(dir: Direction): Option[SpacManWithLife] =
        checkCollisions(moveSpacMan(dir), dir)
