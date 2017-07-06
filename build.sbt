organization := "example"
name := "scala-ddd-base-functional"
version := "1.0.0-SNAPSHOT"

scalaVersion := "2.12.2"

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
)

resolvers ++= Seq(
  "Sonatype OSS Snapshot Repository" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype OSS Release Repository" at "https://oss.sonatype.org/content/repositories/releases/"
)

libraryDependencies ++= Seq(
  "org.scalatest"      %% "scalatest"           % "3.0.1" % Test,
  "org.typelevel"      %% "cats"                % "0.9.0",
  "com.typesafe.slick" %% "slick"               % "3.2.0",
  "org.slf4j"          % "slf4j-nop"            % "1.6.4",
  "com.typesafe.slick" %% "slick-hikaricp"      % "3.2.0",
  "mysql"              % "mysql-connector-java" % "5.1.42",
  "com.github.j5ik2o"  %% "scalatestplus-db"    % "1.0.5" % Test
)

updateOptions := updateOptions.value.withCachedResolution(true)
