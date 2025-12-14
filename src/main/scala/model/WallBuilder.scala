package model

enum BuildDirection:
    case Single
    case Horizontal
    case Vertical
    case Complex

/** Object to understand the build direction between two positions */
object BuildDirection:
    def understandBuildDirection(start: Position2D, end: Position2D): BuildDirection =
        if (start.y == end.y && start.x != end.x) Horizontal
        else if (start.x == end.x && start.y != end.y) Vertical
        else if (start.x == end.x && start.y == end.y) Single
        else Complex

/** Object to build multiple walls between two positions */
object WallBuilder:

    def createWalls(startPos: Position2D, endPos: Position2D): Set[Wall] =
        BuildDirection.understandBuildDirection(startPos, endPos) match
            case BuildDirection.Horizontal => createHorizontalWall(startPos, endPos)
            case BuildDirection.Vertical   => createVerticalWall(startPos, endPos)
            case BuildDirection.Complex    => createComplexWall(startPos, endPos)
            case BuildDirection.Single     => Set(Wall(startPos))

    private def createHorizontalWall(startPos: Position2D, endPos: Position2D): Set[Wall] =
        val (x1, x2) = orderPosition(startPos.x, endPos.x)
        (for
            x <- x1 to x2
        yield Wall(Position2D(x, startPos.y))).toSet

    private def createVerticalWall(startPos: Position2D, endPos: Position2D): Set[Wall] =
        val (y1, y2) = orderPosition(startPos.y, endPos.y)
        (for
            y <- y1 to y2
        yield Wall(Position2D(startPos.x, y))).toSet

    private def createComplexWall(startPos: Position2D, endPos: Position2D): Set[Wall] =
        val (x1, x2) = orderPosition(startPos.x, endPos.x)
        val (y1, y2) = orderPosition(startPos.y, endPos.y)
        (for
            x <- x1 to x2
            y <- y1 to y2
        yield Wall(Position2D(x, y))).toSet

    /** Orders two integers in ascending order
      * @param x1 First integer
      * @param x2 Second integer
      * @return A tuple containing the two integers in ascending order
      */
    private def orderPosition(x1: Int, x2: Int): (Int, Int) =
        if (x1 <= x2) (x1, x2)
        else (x2, x1)
