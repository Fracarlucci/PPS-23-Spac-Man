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

trait Score[E <: Score[E]]:
    val score: Int
    def addScore(points: Int): E =
        if points >= 0 then updateScore(score + points)
        else updateScore(score)
    protected def updateScore(points: Int): E

case class SpacManBasic(val position: Position2D, val direction: Direction, val score: Int)
    extends MovableEntity:

    override def withPosAndDir(newPosition: Position2D, newDirection: Direction): SpacManBasic =
        this.copy(position = newPosition, direction = newDirection)

    def addScore(points: Int): SpacManBasic =
        if points < 0 then this
        else this.copy(score = this.score + points)

    override def equals(obj: Any): Boolean = obj match
        case that: SpacManBasic =>
            this.position == that.position
        case _ => false

case class SpacManWithLife(
    position: Position2D,
    direction: Direction,
    score: Int,
    val lives: Int = DEFAULT_LIVES
) extends MovableEntity with Life[SpacManWithLife] with Score[SpacManWithLife]:

    override def withPosAndDir(newPosition: Position2D, newDirection: Direction): SpacManWithLife =
        this.copy(position = newPosition, direction = newDirection)

    protected def updateLife(newLives: Int): SpacManWithLife = this.copy(lives = newLives)
    
    protected def updateScore(points: Int): SpacManWithLife  = this.copy(score = points)

    override def equals(obj: Any): Boolean = obj match
        case that: SpacManWithLife =>
            this.position == that.position
        case _ => false
