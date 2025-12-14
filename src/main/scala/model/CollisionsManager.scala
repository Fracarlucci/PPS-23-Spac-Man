package model

trait CollisionsManager:
    def detectCollision(
        entities: Set[GameEntity],
        direction: Direction
    ): CollisionType

enum CollisionType:
    case GhostCollision(ghost: GhostBasic)
    case DotBasicCollision(dot: DotBasic)
    case DotPowerCollision(dot: DotPower)
    case DotFruitCollision(fruit: DotFruit)
    case TunnelCollision(tunnel: Tunnel)
    case NoCollision

/** Responsabile del rilevamento delle collisioni tra SpacMan e altre entità.
     * Implementa una priorità specifica: fantasmi > frutti > power dots > dots > tunnel.
     */
class SimpleCollisionsManager(gameManager: GameManager) extends CollisionsManager:

    /** Rileva il tipo di collisione analizzando le entità presenti in una posizione.
      *
      * @param entities Set di entità nella posizione corrente
      * @param direction Direzione del movimento (necessaria per i tunnel)
        * @return Il tipo di collisione rilevato, None se nessuna collisione
        */
    def detectCollision(
        entities: Set[GameEntity],
        direction: Direction
    ): CollisionType =
        import CollisionType.*

        // Priorità delle collisioni: Ghost > Fruit > PowerDot > BasicDot > Tunnel
        findGhost(entities)
            .orElse(findFruit(entities))
            .orElse(findPowerDot(entities))
            .orElse(findBasicDot(entities))
            .orElse(findTunnel(entities, direction))
            .getOrElse(NoCollision)

    private def findGhost(entities: Set[GameEntity]): Option[CollisionType] =
        entities.collectFirst {
            case ghost: GhostBasic => CollisionType.GhostCollision(ghost)
        }

    private def findFruit(entities: Set[GameEntity]): Option[CollisionType] =
        entities.collectFirst {
            case fruit: DotFruit => CollisionType.DotFruitCollision(fruit)
        }

    private def findPowerDot(entities: Set[GameEntity]): Option[CollisionType] =
        entities.collectFirst {
            case dot: DotPower => CollisionType.DotPowerCollision(dot)
        }

    private def findBasicDot(entities: Set[GameEntity]): Option[CollisionType] =
        entities.collectFirst {
            case dot: DotBasic => CollisionType.DotBasicCollision(dot)
        }

    private def findTunnel(
        entities: Set[GameEntity],
        direction: Direction
    ): Option[CollisionType] =
        entities.collectFirst {
            case tunnel: model.Tunnel if tunnel.canTeleport(direction) =>
                CollisionType.TunnelCollision(tunnel)
        }
