name := """svc-forecast"""
organization := "com.joe"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.8"

libraryDependencies += guice //dependency injection
libraryDependencies += ws //web service lib
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
libraryDependencies += "org.scalatestplus" %% "mockito-3-12" % "3.2.10.0" % "test"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.joe.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.joe.binders._"
