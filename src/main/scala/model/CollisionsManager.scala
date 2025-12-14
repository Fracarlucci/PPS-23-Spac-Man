package model

import model.*
import model.map.GameMap
import CollisionType.*

enum CollisionType:
    case GhostCollision(ghost: GhostBasic)
    case DotBasicCollision(dot: DotBasic)
    case DotPowerCollision(dot: DotPower)
    case DotFruitCollision(fruit: DotFruit)
    case TunnelCollision(tunnel: Tunnel)
    case NoCollision

val CHASE_TIME_MS: Long = 10000L

object CollisionsManager:

    def detectCollision(
        entities: Set[GameEntity],
        direction: Direction
    ): CollisionType =
        entities.collectFirst { case g: GhostBasic => GhostCollision(g) }
            .orElse(entities.collectFirst { case f: DotFruit => DotFruitCollision(f) })
            .orElse(entities.collectFirst { case p: DotPower => DotPowerCollision(p) })
            .orElse(entities.collectFirst { case d: DotBasic => DotBasicCollision(d) })
            .orElse(
              entities.collectFirst {
                  case t: Tunnel if t.canTeleport(direction) => TunnelCollision(t)
              }
            )
            .getOrElse(NoCollision)

    def applyCollisionEffect(
        collision: CollisionType,
        direction: Direction,
        gameMap: GameMap,
        spacMan: SpacManWithLife,
        isChaseMode: Boolean,
        addChaseTime: Long => Unit,
        onGameOver: () => Unit
    ): Option[(GameMap, SpacManWithLife)] =
        collision match
            case GhostCollision(ghost) =>
                handleGhostCollision(
                  ghost,
                  gameMap,
                  spacMan,
                  isChaseMode,
                  onGameOver
                )

            case DotBasicCollision(dot) =>
                val updatedMap = gameMap.remove(dot).getOrElse(gameMap)
                val updatedSp  = spacMan.addScore(dot.score)
                Some((updatedMap, updatedSp))

            case DotPowerCollision(dot) =>
                val updatedMap = gameMap.remove(dot).getOrElse(gameMap)
                val updatedSp  = spacMan.addScore(dot.score)
                addChaseTime(CHASE_TIME_MS)
                Some((updatedMap, updatedSp))

            case DotFruitCollision(fruit) =>
                val updatedMap = gameMap.remove(fruit).getOrElse(gameMap)
                val updatedSp  = spacMan.addScore(fruit.score).addLife()
                Some((updatedMap, updatedSp))

            case TunnelCollision(tunnel) =>
                val teleported = spacMan.teleport(tunnel.toPos).asInstanceOf[SpacManWithLife]
                gameMap.replaceEntityTo(spacMan, teleported) match
                    case Right(updated) => Some((updated, teleported))
                    case Left(_)        => None

            case NoCollision =>
                Some((gameMap, spacMan))

    private def handleGhostCollision(
        ghost: GhostBasic,
        gameMap: GameMap,
        spacMan: SpacManWithLife,
        isChaseMode: Boolean,
        onGameOver: () => Unit
    ): Option[(GameMap, SpacManWithLife)] =
        if isChaseMode then
            val updatedSp  = spacMan.addScore(ghost.score)
            val pos        = getRandomSpawnPosition(gameMap)
            val teleported = ghost.teleport(pos).asInstanceOf[GhostBasic]
            val updatedMap =
                gameMap.replaceEntityTo(ghost, teleported).getOrElse(gameMap)
            Some((updatedMap, updatedSp))
        else
            val damaged = spacMan.removeLife()
            if damaged.lives <= 0 then
                onGameOver()
                Some((gameMap.remove(spacMan).getOrElse(gameMap), damaged))
            else
                val respawned =
                    damaged.teleport(gameMap.spawnPoint).asInstanceOf[SpacManWithLife]
                val updatedMap =
                    gameMap.replaceEntityTo(spacMan, respawned).getOrElse(gameMap)
                Some((updatedMap, respawned))

    private def getRandomSpawnPosition(gameMap: GameMap): Position2D =
        val spawnPoints = gameMap.ghostSpawnPoints.toSeq
        val free = spawnPoints.filter { pos =>
            gameMap.entityAt(pos).toOption.forall(!_.exists(_.isInstanceOf[GhostBasic]))
        }
        val available = if free.nonEmpty then free else spawnPoints
        available(scala.util.Random.nextInt(available.size))

    def checkGhostCollision(
        ghost: GhostBasic,
        spacMan: SpacManWithLife,
        gameMap: GameMap,
        isChaseMode: Boolean,
        onGameOver: () => Unit
    ): Option[(GameMap, SpacManWithLife)] =
        if ghost.position != spacMan.position then None
        else
            handleGhostCollision(
              ghost,
              gameMap,
              spacMan,
              isChaseMode,
              onGameOver
            )
