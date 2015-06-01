import sbt.Keys._

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
val angular = "1.4.0"
val angularBinding = "0.2"
val utest = "0.3.0"

lazy val jvm = project.
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq( "biz.enef" %% "surf-akka-rest" % "0.1-SNAPSHOT" )
  )
  

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
      "org.webjars" % "angularjs" % angular / "angular.min.js" dependsOn "jquery.js",
      ProvidedJS / "js/bootstrap-combobox.js" dependsOn "jquery.js"
    ),
   testFrameworks ++= Seq(new TestFramework("utest.runner.Framework"))
  ).
  enablePlugins(ScalaJSPlugin)
  

// standalone application (server+web client in a single fat JAR)
lazy val app = project.in( file(".") ).
  dependsOn( jvm ).
  settings(commonSettings:_*).
  settings(
    // build JS and add JS resources
    (compile in Compile) <<= (compile in Compile).dependsOn(fastOptJS in (js,Compile)),
    mainClass in (Compile,run) := Some("com.oomagnitude.Server")
  )
    

