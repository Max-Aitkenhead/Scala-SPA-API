name := """rest_api"""
organization := "max"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.2"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "org.reactivemongo" %% "reactivemongo" % "0.20.11"
libraryDependencies += "org.mindrot" % "jbcrypt" % "0.4"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "max.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "max.binders._"

//PlayKeys.devSettings += "play.akka.dev-mode.akka.cluster.log-info" -> "off"
//PlayKeys.devSettings += "play.filters.hosts" -> "allowed = [\".\"]"


