ThisBuild / scalaVersion := "3.3.5"

lazy val root = project
  .in(file("."))
  .settings(
    name := "spac-man",
    version := "0.1.0-SNAPSHOT",
    libraryDependencies += "com.github.sbt" % "junit-interface" % "0.13.2" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test,
    libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
    libraryDependencies += "org.jline" % "jline" % "3.25.1"
  )

scalacOptions ++= Seq("-Wunused:all", "-Werror")

inThisBuild(
  List(
    scalaVersion := "3.3.5",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision
  )
)