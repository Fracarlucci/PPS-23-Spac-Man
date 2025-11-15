package model

import model.map.GameMap

trait GameManager:
    def updateEntityPositions(entities: Set[GameEntity]): Unit
    def isWin(): Boolean
    def getScore(): Int
    def getGameMap(): GameMap
    def movePlayer(newDirection: Direction): SpacManBasic
    def moveGhosts(): Set[GhostBasic]
    def removeDotAt(position: Position2D): Unit

class SimpleGameManager(
    var spacMan: SpacManBasic,
    var ghosts: Set[GhostBasic],
    var dots: Set[DotBasic],
    gameMap: GameMap,
    score: Int
    ) extends GameManager:

    override def updateEntityPositions(entities: Set[GameEntity]): Unit = 
        gameMap.placeAll(entities)
    override def isWin(): Boolean = dots.isEmpty
    override def getScore(): Int = score
    override def getGameMap(): GameMap = gameMap
    override def movePlayer(newDirection: Direction): SpacManBasic = 
        if gameMap.canMove(spacMan, newDirection) then
            spacMan = spacMan.move(newDirection).asInstanceOf[SpacManBasic] 
        spacMan

    override def moveGhosts(): Set[GhostBasic] = 
        def findValidMove(ghost: GhostBasic): GhostBasic =
            val nextDirection = ghost.nextMove()
            if gameMap.canMove(ghost, nextDirection) then
                ghost.move(nextDirection).asInstanceOf[GhostBasic]
            else
                findValidMove(ghost)
        
        ghosts = ghosts.map(findValidMove)
        ghosts

    override def removeDotAt(position: Position2D): Unit = 
        dots = dots.filterNot(dot => dot.position == position)
        updateEntityPositions()

sealed trait CollisionsType
object CollisionsType:
    case object ghostCollision extends CollisionsType
    case object dotCollision extends CollisionsType

trait CollisionManager:
    def checkCollisions(
        spacMan: SpacManBasic,
        ghosts: Set[GhostBasic],
        dots: Set[DotBasic]
    ): (Boolean)
    def handleghostCollision(): Unit
    def handledotCollision(): Unit

class SimpleCollisionManager() extends CollisionManager:

    override def checkCollisions(
        spacMan: SpacManBasic,
        ghosts: Set[GhostBasic],
        dots: Set[DotBasic]
    ): Option[CollisionsType] =
        ghosts.find(_.position == spacMan.position).map(_ => CollisionsType.ghostCollision)
            .orElse(dots.find(_.position == spacMan.position).map(_ => CollisionsType.dotCollision))

    override def handleghostCollision(): Unit = 
        GameState.setGameOver(true)
        
    override def handledotCollision(position: Position2D, spacMan: SpacManBasic, dot: DotBasic): Unit =
        spacMan.addScore(dot.score())
        GameManager.removeDotAt(position)
    