package model

import model.map.GameMap
import scala.annotation.tailrec

trait GameManager:
    def getGameMap: GameMap
    def getSpacMan: SpacManBasic
    def isWin(): Boolean
    def isGameOver(): Boolean
    def moveSpacManAndCheck(newDirection: Direction): Option[SpacManBasic]
    def moveGhosts(): Set[GhostBasic]

case class SimpleGameManager(
    private var _spacMan: SpacManBasic,
    private var _gameMap: GameMap,
    private var _gameOver: Boolean = false
) extends GameManager:

    def getSpacMan: SpacManBasic = _spacMan

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

    private def moveSpacMan(newDirection: Direction): SpacManBasic =
        if _gameMap.canMove(_spacMan, newDirection) then
            val movedSpacMan = _spacMan.move(newDirection).asInstanceOf[SpacManBasic]
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
        case TunnelCollision(tunnel: Tunnel)
        case NoCollision

    private def detectCollision(
        entities: Set[GameEntity],
        direction: Direction
    ): CollisionResult =
        import CollisionResult.*
        entities.collectFirst { case ghost: GhostBasic => GhostCollision }
            .orElse(entities.collectFirst { case dot: DotBasic => DotCollision(dot) })
            .orElse(
              entities.collectFirst { case tunnel: Tunnel => tunnel }
                  .filter(_.canTeleport(direction))
                  .map(TunnelCollision.apply)
            )
            .getOrElse(NoCollision)

    private def applyCollisionEffect(
        spacMan: SpacManBasic,
        collision: CollisionResult
    ): Option[SpacManBasic] =
        import CollisionResult.*
        collision match
            case GhostCollision =>
                handleGhostCollision()
                None
            case DotCollision(dot) =>
                _gameMap = _gameMap.remove(dot).getOrElse(_gameMap)
                _spacMan = _spacMan.addScore(dot.score)
                Some(_spacMan)
            case TunnelCollision(tunnel) =>
                val teleportedSpacMan = spacMan.teleport(tunnel.toPos)
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
        _gameMap = _gameMap.remove(_spacMan).getOrElse(_gameMap)
        _gameOver = true

    private def checkCollisionWithGhost(ghost: GhostBasic): Unit =
        if ghost.position == _spacMan.position then
            handleGhostCollision()

    private def checkCollisions(
        spacMan: SpacManBasic,
        direction: Direction
    ): Option[SpacManBasic] =
        _gameMap.entityAt(spacMan.position).toOption
            .map(entities => detectCollision(entities, direction))
            .map(collision => applyCollisionEffect(spacMan, collision))
            .getOrElse(Some(_spacMan))

    def moveSpacManAndCheck(dir: Direction): Option[SpacManBasic] =
        checkCollisions(moveSpacMan(dir), dir)
