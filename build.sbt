name := "play-cms-root"

organization := "org.qirx"

scalaVersion := "2.11.2"

unmanagedSourceDirectories in Compile := Seq.empty

unmanagedSourceDirectories in Test := Seq.empty

managedSourceDirectories in Compile := Seq.empty

managedSourceDirectories in Test := Seq.empty

lazy val commonSettings = 
  onlyScalaSources ++
  Seq(
    organization := "org.qirx",
    scalaVersion := "2.11.2",
    resolvers += Resolver.typesafeRepo("releases")
  )

lazy val scalaProjectSettings = 
  Seq(
	testFrameworks += new TestFramework("org.qirx.littlespec.sbt.TestFramework"),
    libraryDependencies ++= Seq(
      littleSpec
    )
  )

// PROJECTS

lazy val `play-cms-root` = project
  .in( file(".") )
  .aggregate(
    `play-cms`, 
    `play-cms-testing`,
    `play-cms-elasticsearch`,
    `play-cms-ui`
  )

lazy val `play-cms` = project
  .in( file("cms") )
  .settings(commonSettings ++ scalaProjectSettings :_*)
  .settings(
    name := "play-cms",
	libraryDependencies ++= Seq(
	  play,
	  playJson,
	  playWs,
	  playTest
	)
  )
  .settings(documentationAt(_ / "documentation") :_*)

lazy val `play-cms-testing` = project
  .in( file("testing") )
  .settings(commonSettings ++ scalaProjectSettings :_*)
  .settings(
    name := "play-cms-testing",
    libraryDependencies ++= Seq(
      play,
      playJson
    )
  )
  .settings(documentationAt(_ / "documentation/testing") :_*)
  .dependsOn(`play-cms`)
  
lazy val `play-cms-elasticsearch` = project
  .in( file("elasticsearch") )
  .settings(commonSettings ++ scalaProjectSettings:_*)
  .settings(
    name := "play-cms-elasticsearch",
    libraryDependencies ++= Seq(
      playJson,
      playWs,
      playTest
    )
  )
  .settings(documentationAt(_ / "documentation/elasticsearch") :_*)
  .dependsOn(`play-cms`, `play-cms-testing` % "test->compile")

lazy val `play-cms-ui` = project
  .in( file("ui") )
  .settings(commonSettings:_*)
  .settings(
    name := "play-cms-ui"
  )

// COMMON DEPENDENCIES

lazy val play = "com.typesafe.play" %% "play" % "2.3.4"

lazy val playJson = "com.typesafe.play" %% "play-json" % "2.3.4"

lazy val playWs = "com.typesafe.play" %% "play-ws" % "2.3.4"

lazy val playTest = "com.typesafe.play" %% "play-test" % "2.3.4" % "test"

lazy val littleSpec = "org.qirx" %% "little-spec" % "0.4-SNAPSHOT" % "test"

lazy val littleSpecDocumentation = "org.qirx" %% "little-spec-extra-documentation" % "0.4-SNAPSHOT" % "test"

// UTILS

def metadataSources(configuration:Configuration, config:String) = 
  unmanagedSourceDirectories in configuration += rootDirectory.value / "metadata" / "src" / config / "scala" 

lazy val rootDirectory = baseDirectory in ThisBuild

lazy val onlyScalaSources = Seq(
  unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value),
  unmanagedSourceDirectories in Test := Seq((scalaSource in Test).value)
)

def documentationAt(location:File => File) = Seq(
	libraryDependencies += littleSpecDocumentation,
    testOptions += Tests.Argument("reporter", "org.qirx.littlespec.reporter.MarkdownReporter"),
    testOptions += Tests.Argument("documentationTarget", location(rootDirectory.value).getAbsolutePath)
)