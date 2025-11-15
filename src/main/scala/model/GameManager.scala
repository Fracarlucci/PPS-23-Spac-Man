package model

import model.map.GameMap

trait GameManager:
    def update(): GameManager
    def updateMap(): Unit
    def isWin(): Boolean
    def getEntities(): List[GameEntity]
    def getMovableEntities(): List[MovableEntity]
    def getSpacMan(): SpacManBasic
    def getGhosts(): List[MovableEntity]
    def getDots(): List[DotBasic]
    def getScore(): Int
    def getGameMap(): GameMap
    def movePlayer(newDirection: Direction): SpacManBasic
    def moveGhosts(): List[GhostBasic]
    def eatDotAt(position: Position2D): Unit

class SimpleGameManager(
    spacMan: SpacManBasic,
    ghosts: List[GhostBasic],
    var dots: List[DotBasic],
    gameMap: GameMap,
    score: Int
    ) extends GameManager:

    override def update(): GameManager = ???
    override def updateMap(): Unit = 
        getMovableEntities().foreach( entity => gameMap.place(entity.position, entity) )
    override def isWin(): Boolean = dots.isEmpty
    override def getEntities(): List[GameEntity] = spacMan :: ghosts ::: dots
    override def getMovableEntities(): List[MovableEntity] = spacMan :: ghosts
    override def getSpacMan(): SpacManBasic = spacMan
    override def getGhosts(): List[MovableEntity] = ghosts
    override def getDots(): List[DotBasic] = dots
    override def getScore(): Int = score
    override def getGameMap(): GameMap = gameMap
    override def movePlayer(newDirection: Direction): SpacManBasic = spacMan.move(newDirection).asInstanceOf[SpacManBasic] 
    override def moveGhosts(): List[GhostBasic] = 
        ghosts.foldLeft(List.empty[GhostBasic])( (acc, ghost) => acc :+ if gameMap.canMove(ghost, ghost.nextMove()) then ghost.move(ghost.nextMove()).asInstanceOf[GhostBasic] else ghost )
    override def eatDotAt(position: Position2D): Unit = 
        val dotOpt = dots.find( dot => dot.position == position )
        dotOpt match
            case Some(dot) => spacMan.addScore(dot.score);
                dots = dots.filterNot( _ == dot )
                updateMap()
            case None => None