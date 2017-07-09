lazy val commonSettings = Seq(
  organization := "com.github.j5ik2o",
  version := "1.0.0-SNAPSHOT",
  scalaVersion := "2.12.2",
  crossScalaVersions := Seq("2.11.11", "2.12.2"),
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-unchecked",
    "-encoding",
    "UTF-8",
    "-Xfatal-warnings",
    "-language:_",
    "-Ywarn-adapted-args" // Warn if an argument list is modified to match the receiver
    ,
    "-Ywarn-dead-code" // Warn when dead code is identified.
    ,
    "-Ywarn-inaccessible" // Warn about inaccessible types in method signatures.
    ,
    "-Ywarn-infer-any" // Warn when a type argument is inferred to be `Any`.
    ,
    "-Ywarn-nullary-override" // Warn when non-nullary `def f()' overrides nullary `def f'
    ,
    "-Ywarn-nullary-unit" // Warn when nullary methods return Unit.
    ,
    "-Ywarn-numeric-widen" // Warn when numerics are widened.
    ,
    "-Ywarn-unused" // Warn when local and private vals, vars, defs, and types are are unused.
    ,
    "-Ywarn-unused-import" // Warn when imports are unused.
  ),
  resolvers ++= Seq(
    "Sonatype OSS Snapshot Repository" at "https://oss.sonatype.org/content/repositories/snapshots/",
    "Sonatype OSS Release Repository" at "https://oss.sonatype.org/content/repositories/releases/",
    "Seasar2 Repository" at "http://maven.seasar.org/maven2"
  ),
  libraryDependencies ++= Seq(
    "org.scalatest"  %% "scalatest"           % "3.0.1" % Test,
    "mysql"          % "mysql-connector-java" % "5.1.42" % Test,
    "ch.qos.logback" % "logback-classic"      % "1.2.3" % "provided",
    "org.slf4j"      % "slf4j-api"            % "1.7.21"
  ),
  updateOptions := updateOptions.value.withCachedResolution(true)
)

lazy val core = (project in file("core"))
  .settings(commonSettings)
  .settings(
    name := "scala-ddd-base-functional-core"
  )

lazy val test = (project in file("test"))
  .settings(commonSettings)
  .settings(
    name := "scala-ddd-base-functional-test",
    libraryDependencies ++= Seq(
      "com.github.j5ik2o" %% "scalatestplus-db" % "1.0.5" % Test
    )
  )
  .dependsOn(core)

lazy val cats = (project in file("cats"))
  .settings(commonSettings)
  .settings(
    name := "scala-ddd-base-functional-cats",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats" % "0.9.0"
    )
  )
  .dependsOn(core)

lazy val skinnyOrm = (project in file("skinny-orm"))
  .settings(commonSettings)
  .settings(
    name := "scala-ddd-base-functional-skinny-orm",
    libraryDependencies ++= Seq(
      "org.skinny-framework" %% "skinny-orm" % "2.3.7"
    )
  )
  .dependsOn(core, cats, test % "test->test")

lazy val slick = (project in file("slick"))
  .settings(commonSettings)
  .settings(
    name := "scala-ddd-base-functional-slick",
    libraryDependencies ++= Seq(
      "com.typesafe.slick" %% "slick"          % "3.2.0",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.2.0"
    )
  )
  .dependsOn(core, cats, test % "test->test")

lazy val example = (project in file("example"))
  .settings(commonSettings)
  .settings(
    name := "scala-ddd-base-functional-example"
  )
  .dependsOn(core,
             cats,
             slick     % "compile->compile;test->test",
             skinnyOrm % "compile->compile;test->test",
             test      % "test->test")

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    name := "scala-ddd-base-functional"
  )
  .aggregate(core, cats, slick, example)
