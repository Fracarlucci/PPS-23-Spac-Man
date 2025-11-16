package model

import model.map.GameMap
import scala.annotation.tailrec

trait GameManager:
    def gameOver: Boolean
    def isWin(): Boolean
    def setGameOver(): Unit
    def moveSpacManAndCheck(newDirection: Direction): Option[SpacManBasic]
    def moveGhosts(): Set[GhostBasic]

case class SimpleGameManager(
    private var _spacMan: SpacManBasic,
    private var _gameMap: GameMap,
    private var _gameOver: Boolean = false
) extends GameManager:

    def spacMan: SpacManBasic = _spacMan

    def gameMap: GameMap = _gameMap

    def gameOver: Boolean = _gameOver

    override def isWin(): Boolean = gameMap.getDots.isEmpty
    
    override def setGameOver(): Unit = _gameOver = true

    override def moveGhosts(): Set[GhostBasic] = 
        @tailrec
        def findValidMove(ghost: GhostBasic, attempts: Int = 10): GhostBasic =
            if attempts <= 0 then ghost
            else
                val nextDirection = ghost.nextMove()
                // println(gameMap.canMove(ghost, nextDirection))
                if gameMap.canMove(ghost, nextDirection) then
                    println(s"Moving ghost from ${ghost.position} to ${ghost.move(nextDirection).position}")
                    ghost.move(nextDirection).asInstanceOf[GhostBasic]
                else
                    findValidMove(ghost, attempts - 1)
        
        gameMap.getGhosts.map(findValidMove(_))

    private def moveSpacMan(newDirection: Direction): SpacManBasic = 
        if gameMap.canMove(_spacMan, newDirection) then
            _spacMan = _spacMan.move(newDirection).asInstanceOf[SpacManBasic] 
        _spacMan

    private def checkCollisions(
        spacMan: SpacManBasic
    ): Option[SpacManBasic] =
        gameMap.entityAt(spacMan.position).toOption.flatMap: entities =>
            entities.collectFirst { case ghost: GhostBasic => ghost }
                .map(_ => { setGameOver(); None })
                .orElse {
                    entities.collectFirst { case dot: DotBasic => dot }
                        .map { dot =>
                            _gameMap = _gameMap.remove(dot).getOrElse(_gameMap)
                            _spacMan = _spacMan.addScore(dot.score)
                            Some(_spacMan)
                        }
                }
                .getOrElse(Some(_spacMan))

    def moveSpacManAndCheck(dir: Direction): Option[SpacManBasic] =
        checkCollisions(moveSpacMan(dir))
