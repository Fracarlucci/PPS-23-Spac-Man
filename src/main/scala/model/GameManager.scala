package model

import model.map.GameMap

trait GameManager:
    def updateEntityPositions(entities: Set[GameEntity]): Unit
    def isWin(): Boolean
    def getScore(): Int
    def getGameMap(): GameMap
    def movePlayer(newDirection: Direction): SpacManBasic
    def moveGhosts(): Set[GhostBasic]
    def setGameOver(): Unit
    def checkCollisions(spacMan: SpacManBasic): Option[SpacManBasic]

class SimpleGameManager(
    var spacMan: SpacManBasic,
    var ghosts: Set[GhostBasic],
    var dots: Set[DotBasic],
    gameMap: GameMap,
    score: Int,
    var gameOver: Boolean = false
) extends GameManager:

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

    override def setGameOver(): Unit = gameOver = true

    override def checkCollisions(
        spacMan: SpacManBasic
    ): Option[SpacManBasic] =
        gameMap.entityAt(spacMan.position).toOption.flatMap: entities =>
            entities.collectFirst { case ghost: GhostBasic => ghost }
                .map(_ => { setGameOver(); None })
                .orElse {
                    entities.collectFirst { case dot: DotBasic => dot }
                        .map { dot =>
                            gameMap.remove(dot)
                            Some(spacMan.addScore(dot.score))
                        }
                }
                .getOrElse(Some(spacMan))
