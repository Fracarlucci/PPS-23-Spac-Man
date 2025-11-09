package model

import model.map.GameMap
import model.map.GameMapImpl
        

object board:
    def apply(width: Int, height: Int): GameMap = GameMapImpl(width, height)

class MapDSL(var map: GameMap):
    
    object place:
        def a(e: GameEntity): GameEntityBuilder = GameEntityBuilder(e)

    class GameEntityBuilder(e: GameEntity):
            infix def at(pos: Position2D): Unit = 
                map = map.place(pos, e)

    /** DSL helper per scrivere at(x, y) */
    object position:
        def apply(x: Int, y: Int): Position2D = Position2D(x, y)



