package model

import model.map.GameMap
import scala.annotation.tailrec

trait GameManager:
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
                    // ghost.move(nextDirection).asInstanceOf[GhostBasic]
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

    private def checkCollisions(
        spacMan: SpacManBasic
    ): Option[SpacManBasic] =
        _gameMap.entityAt(spacMan.position).toOption.flatMap: entities =>
            entities.collectFirst { case ghost: GhostBasic => ghost }
                .map(_ => { 
                    _gameMap = _gameMap.remove(spacMan).getOrElse(_gameMap)
                    _gameOver = true
                    None 
                })                
                .orElse {
                    entities.collectFirst { case dot: DotBasic => dot }
                        .map { dot =>
                            _gameMap = _gameMap.remove(dot).getOrElse(_gameMap)
                            // TODO: le opzioni sono due, o si aggiorna la mappa prima di impostare lo score
                            //       oppure si modifica la equals dello spacman
                            //       in modo che non controlli che lo score sia identico 
                            _spacMan = _spacMan.addScore(dot.score)
                            Some(_spacMan)
                        }
                }
                .getOrElse(Some(_spacMan))

    def moveSpacManAndCheck(dir: Direction): Option[SpacManBasic] =
        checkCollisions(moveSpacMan(dir))
