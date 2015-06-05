//enablePlugins(JavaAppPackaging)

import sbt.Keys._
import spray.revolver.RevolverPlugin._

name := "scalajs-dashboard"

val commonSettings = Seq(
  version := "0.1",
  scalaVersion := "2.11.6",
  scalacOptions ++= Seq("-deprecation","-feature","-Xlint"),
  resolvers += "karchedon-repo" at "http://maven.karchedon.de/",
  resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

scalaJSStage in Global := FastOptStage

val angulateDebugFlags = Seq(
  "runtimeLogging"
).map( f => s"-Xmacro-settings:biz.enef.angulate.$f" )

val jquery = "2.1.3"
val jqueryBinding = "0.8.0"
val bootstrap = "3.3.4"
val angular = "1.3.15"
val angularBinding = "0.2"
val utest = "0.3.0"
val akkaStream = "1.0-RC2"


lazy val js = project.
  settings(commonSettings: _*).
  settings(
    scalacOptions ++= angulateDebugFlags,
    // JavaScript dependencies concatenated to a single file; The resulting file
    // in the target folder will have the suffix -jsdeps.js
    skip in packageJSDependencies := false,
    persistLauncher in Compile := true,
    persistLauncher in Test := false,
    libraryDependencies ++= Seq(
      "org.webjars" % "jquery" % jquery,
      "org.webjars" % "bootstrap" % bootstrap,
      "be.doeraene" %%% "scalajs-jquery" % jqueryBinding,
      "biz.enef" %%% "scalajs-angulate" % angularBinding,
      "com.lihaoyi" %%% "utest" % utest % "test"
    ),
    jsDependencies ++= Seq(
      // Makes it possible to run the application from the sbt console (i.e., switches runtime to PhantomJS)
      RuntimeDOM,
      "org.webjars" % "jquery" % jquery / "jquery.js",
      "org.webjars" % "bootstrap" % bootstrap / "bootstrap.min.js" dependsOn "jquery.js",
      "org.webjars.bower" % "angular" % angular / "angular.min.js" dependsOn "jquery.js",
      ProvidedJS / "ng-websocket.js" dependsOn "angular.min.js",
      ProvidedJS / "bootstrap-combobox.js" dependsOn "jquery.js",
      "org.webjars" % "d3js" % "3.5.5" / "d3.min.js",
      "org.webjars" % "nvd3" % "1.7.1" / "nv.d3.min.js" dependsOn "d3.min.js",
//      "org.webjars" % "angular-nvd3" % "0.1.1" / "angular-nvd3.min.js" dependsOn "nv.d3.min.js" dependsOn "angular.min.js"
      ProvidedJS / "angular-nvd3.min.js" dependsOn "nv.d3.min.js" dependsOn "angular.min.js"
    ),
   testFrameworks ++= Seq(new TestFramework("utest.runner.Framework"))
  ).enablePlugins(ScalaJSPlugin)

lazy val jvm = project.
  settings(commonSettings: _*).
  settings(Revolver.settings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http-scala-experimental" % akkaStream,
      "com.typesafe.akka" %% "akka-http-spray-json-experimental"    % akkaStream),
    (resourceGenerators in Compile) <+=
      (fastOptJS in Compile in js, packageScalaJSLauncher in Compile in js)
        .map((f1, f2) => Seq(f1.data, f2.data)),
    watchSources <++= (watchSources in js)
  )

lazy val root = project.in(file(".")).aggregate(js, jvm)

