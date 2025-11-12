package model

enum BuildDirection:
  case Single, Horizontal, Vertical, Complex

object BuildDirection:
  def understandBuildDirection(start: Position2D, end: Position2D): BuildDirection =
    if (start.y == end.y && start.x != end.x) Horizontal
    else if (start.x == end.x && start.y != end.y) Vertical
    else if (start.x == end.x && start.y == end.y) Single
    else Complex

object WallBuilder:

  def createWalls(startPos: Position2D, endPos: Position2D): Set[Wall] =
    BuildDirection.understandBuildDirection(startPos, endPos) match
      case BuildDirection.Horizontal => createHorizontalWall(startPos, endPos)
      case BuildDirection.Vertical   => createVerticalWall(startPos, endPos)
      case BuildDirection.Complex    => createComplexWall(startPos, endPos)
      case BuildDirection.Single     => Set(Wall(startPos))

  private def createHorizontalWall(startPos: Position2D, endPos: Position2D): Set[Wall] =
    val (x1, x2) =
      if (startPos.x <= endPos.x) (startPos.x, endPos.x)
      else (endPos.x, startPos.x)
    (for
      x <- x1 to x2
    yield Wall(Position2D(x, startPos.y))).toSet

  private def createVerticalWall(startPos: Position2D, endPos: Position2D): Set[Wall] =
    val (y1, y2) =
      if (startPos.y <= endPos.y) (startPos.y, endPos.y)
      else (endPos.y, startPos.y)

    (for
      y <- y1 to y2
    yield Wall(Position2D(startPos.x, y))).toSet

  private def createComplexWall(startPos: Position2D, endPos: Position2D): Set[Wall] =
    val (x1, x2) =
      if (startPos.x <= endPos.x) (startPos.x, endPos.x)
      else (endPos.x, startPos.x)

    val (y1, y2) =
      if (startPos.y <= endPos.y) (startPos.y, endPos.y)
      else (endPos.y, startPos.y)

    (for
      x <- x1 to x2
      y <- y1 to y2
    yield Wall(Position2D(x, y))).toSet
