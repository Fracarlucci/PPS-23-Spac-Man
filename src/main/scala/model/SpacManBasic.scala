package model

private val DEFAULT_LIVES = 3

/** E <: Life[E] sarebbe un type check, altrimenti E potrebbe non aver implementato Life e in quel caso darebbe errori Runtime */
trait Life[E <: Life[E]]:
    val lives: Int
    def addLife(): E =
        val newLives = lives + 1
        updateLife(newLives)
    def removeLife(): E =
        require(lives > 0)
        val newLives = lives - 1
        updateLife(newLives)
    protected def updateLife(newLives: Int): E

case class SpacManBasic(val position: Position2D, val direction: Direction, val score: Int)
    extends MovableEntity:

    override def withPosAndDir(newPosition: Position2D, newDirection: Direction): SpacManBasic =
        this.copy(position = newPosition, direction = newDirection)

    def addScore(points: Int): SpacManBasic =
        if points < 0 then this
        else this.copy(score = this.score + points)

    def teleport(destination: Position2D): SpacManBasic =
        this.copy(position = destination)

    override def equals(obj: Any): Boolean = obj match
        case that: SpacManBasic =>
            this.position == that.position
        case _ => false

case class SpacManWithLife(position: Position2D, direction: Direction, score: Int, val lives: Int = DEFAULT_LIVES)
    extends MovableEntity with Life[SpacManWithLife]:

    override def withPosAndDir(newPosition: Position2D, newDirection: Direction): MovableEntity =
        this.copy(position = newPosition, direction = newDirection)

    protected def updateLife(newLives: Int): SpacManWithLife = this.copy(lives = newLives)

    override def equals(obj: Any): Boolean = obj match
        case that: SpacManBasic =>
            this.position == that.position
        case _ => false
