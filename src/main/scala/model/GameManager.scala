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

case class SimpleGameManager(
    private var _spacMan: SpacManWithLife,
    private var _gameMap: GameMap,
    private var _gameOver: Boolean = false
) extends GameManager:

    def getSpacMan: SpacManWithLife = _spacMan

    def getGameMap: GameMap = _gameMap

    override def isWin(): Boolean = _gameMap.getDots.isEmpty

    override def isGameOver(): Boolean = _gameOver

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
                            checkCollisionWithGhost(movedGhost)
                            movedGhost
                        case Left(error) =>
                            println(s"Error moving Ghost: $error")
                            ghost
                else
                    findValidMove(ghost, attempts - 1)

        _gameMap.getGhosts.map(findValidMove(_))

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
        case GhostCollision
        case DotCollision(dot: DotBasic)
        case DotFruitCollision(fruit: DotFruit)
        case TunnelCollision(tunnel: Tunnel)
        case NoCollision

    private def detectCollision(
        entities: Set[GameEntity],
        direction: Direction
    ): CollisionResult =
        import CollisionResult.*
        entities.collectFirst { case ghost: GhostBasic => GhostCollision }
            .orElse(entities.collectFirst { case dot: DotBasic => DotCollision(dot) })
            .orElse(entities.collectFirst { case fruit: DotFruit => DotFruitCollision(fruit) })
            .orElse(
              entities.collectFirst { case tunnel: Tunnel => tunnel }
                  .filter(_.canTeleport(direction))
                  .map(TunnelCollision.apply)
            )
            .getOrElse(NoCollision)

    private def applyCollisionEffect(
        spacMan: SpacManWithLife,
        collision: CollisionResult
    ): Option[SpacManWithLife] =
        import CollisionResult.*
        collision match
            case GhostCollision =>
                handleGhostCollision()
                None
            case DotCollision(dot) =>
                _gameMap = _gameMap.remove(dot).getOrElse(_gameMap)
                _spacMan = _spacMan.addScore(dot.score)
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

    private def handleGhostCollision(): Unit =
        println("Entro qui")
        _spacMan = _spacMan.removeLife()
        _gameOver = isSpacManDead()
        _gameOver match
            case false =>
                val teleportedSpacMan = handleRemoveSpacManLife()
                _gameMap = _gameMap.replaceEntityTo(_spacMan, teleportedSpacMan).getOrElse(_gameMap)
                _spacMan = teleportedSpacMan
            case true =>
                _gameMap = _gameMap.remove(_spacMan).getOrElse(_gameMap)

    private def isSpacManDead(): Boolean = _spacMan.lives == 0

    private def handleRemoveSpacManLife(): SpacManWithLife =
        _spacMan.teleport(_gameMap.spawnPoint).asInstanceOf[SpacManWithLife]

    private def checkCollisionWithGhost(ghost: GhostBasic): Unit =
        if ghost.position == _spacMan.position then
            handleGhostCollision()

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
