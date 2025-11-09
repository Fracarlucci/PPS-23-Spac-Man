package model

sealed trait GameEntity

case object Wall extends GameEntity
case object PacMan extends GameEntity
case object Ghost extends GameEntity


