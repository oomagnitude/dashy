enablePlugins(ScalaJSPlugin)

import sbt.Keys._
import spray.revolver.RevolverPlugin._

lazy val root = project.in(file(".")).
  aggregate(dashJS, dashJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val dash = crossProject.in(file(".")).
  settings(
    name := "scalajs-dashboard",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.11.7",
    scalacOptions ++= Seq("-deprecation","-feature","-Xlint"),
    unmanagedSourceDirectories in Compile += baseDirectory.value  / "shared" / "main" / "scala",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "autowire" % "0.2.5",
      "com.lihaoyi" %%% "scalatags" % "0.5.2",
      "com.lihaoyi" %%% "upickle" % "0.3.0",
      "com.lihaoyi" %%% "utest" % "0.3.0" % "test",
      "com.github.japgolly.scalacss" %%% "core" % "0.3.0",
      "com.github.japgolly.scalacss" %%% "ext-scalatags" % "0.3.0",
      "com.oomagnitude" %%% "metrics-shared" % "0.1-SNAPSHOT"
    ),
    testFrameworks += new TestFramework("utest.runner.Framework")
  )
  .jvmSettings(
    // Add JVM-specific settings here
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http-experimental" % "1.0-RC4",
      "com.typesafe.akka" %% "akka-stream-experimental" % "1.0-RC4"
    )
  )
  .jsSettings(
    skip in packageJSDependencies := false,
    libraryDependencies ++= Seq(
      "be.doeraene" %%% "scalajs-jquery" % "0.8.0",
      "org.scala-js" %%% "scalajs-dom" % "0.8.0",
      "com.lihaoyi" %%% "scalarx" % "0.2.8"
    ),
    jsDependencies ++= Seq(
      // Makes it possible to run the application from the sbt console (i.e., switches runtime to PhantomJS)
      RuntimeDOM)
  )

scalaJSStage in Global := FastOptStage

lazy val dashJS = dash.js
lazy val dashJVM = dash.jvm.settings(
  (resourceGenerators in Compile) <+=
    (fastOptJS in Compile in dashJS).map((f1) => Seq(f1.data)),
  watchSources <++= (watchSources in dashJS)
).settings(Revolver.settings: _*)

