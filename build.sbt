import scala.concurrent.duration._
val compileScalaStyle = taskKey[Unit]("compileScalaStyle")

lazy val scalaStyleSettings = Seq(
  (scalastyleConfig in Compile) := file("scalastyle-config.xml"),
  compileScalaStyle := scalastyle.in(Compile).toTask("").value,
  (compile in Compile) := (compile in Compile).dependsOn(compileScalaStyle).value
)

val coreSettings = Seq(
  sonatypeProfileName := "com.github.j5ik2o",
  organization := "com.github.j5ik2o",
  scalaVersion := "2.12.6",
  crossScalaVersions ++= Seq("2.11.11", "2.12.6"),
  scalacOptions ++= {
    Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-encoding",
      "UTF-8",
      "-language:existentials",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-language:higherKinds"
    ) ++ {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2L, scalaMajor)) if scalaMajor == 12 =>
          Seq.empty
        case Some((2L, scalaMajor)) if scalaMajor <= 11 =>
          Seq(
            "-Yinline-warnings"
          )
      }
    }
  },
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ =>
    false
  },
  pomExtra := {
    <url>https://github.com/j5ik2o/scala-ddd-base</url>
      <licenses>
        <license>
          <name>The MIT License</name>
          <url>http://opensource.org/licenses/MIT</url>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:j5ik2o/scala-ddd-base.git</url>
        <connection>scm:git:github.com/j5ik2o/scala-ddd-base</connection>
        <developerConnection>scm:git:git@github.com:j5ik2o/scala-ddd-base.git</developerConnection>
      </scm>
      <developers>
        <developer>
          <id>j5ik2o</id>
          <name>Junichi Kato</name>
        </developer>
      </developers>
  },
  publishTo in ThisBuild := sonatypePublishTo.value,
  credentials := {
    val ivyCredentials = (baseDirectory in LocalRootProject).value / ".credentials"
    Credentials(ivyCredentials) :: Nil
  },
  scalafmtOnCompile in ThisBuild := true,
  scalafmtTestOnCompile in ThisBuild := true,
  resolvers ++= Seq(
    "Sonatype OSS Snapshot Repository" at "https://oss.sonatype.org/content/repositories/snapshots/",
    "Sonatype OSS Release Repository" at "https://oss.sonatype.org/content/repositories/releases/"
  ),
  libraryDependencies ++= Seq(
    "org.typelevel"     %% "cats-core"        % "1.1.0",
    "org.typelevel"     %% "cats-free"        % "1.1.0",
    "com.beachape"      %% "enumeratum"       % "1.5.13",
    "org.slf4j"         % "slf4j-api"         % "1.7.25",
    "org.scalatest"     %% "scalatest"        % "3.0.5" % Test,
    "org.scalacheck"    %% "scalacheck"       % "1.14.0" % Test,
    "ch.qos.logback"    % "logback-classic"   % "1.2.3" % Test,
    "com.github.j5ik2o" %% "scalatestplus-db" % "1.0.5" % Test
  )
) ++ scalaStyleSettings

val circeVersion    = "0.10.0-M1"
val akkaHttpVersion = "10.1.1"
val akkaVersion     = "2.5.11"

lazy val core = (project in file("core")).settings(
  coreSettings ++ Seq(
    name := "scala-ddd-base-core",
    libraryDependencies ++= Seq(
      )
  )
)

val dbDriver     = "com.mysql.jdbc.Driver"
val dbName       = "dddbase"
val dbUser       = "dddbase"
val dbPassword   = "passwd"
val dbPort       = 3310
val dbUrl        = s"jdbc:mysql://localhost:$dbPort/$dbName?useSSL=false"
val slickVersion = "3.2.0"

lazy val slick = (project in file("jdbc/slick")).settings(
  coreSettings ++ Seq(
    name := "scala-ddd-base-slick",
    libraryDependencies ++= Seq(
      "io.monix"           %% "monix"          % "3.0.0-RC1",
      "com.typesafe.slick" %% "slick"          % slickVersion,
      "com.typesafe.slick" %% "slick-hikaricp" % slickVersion
    )
  )
) dependsOn core

lazy val skinny = (project in file("jdbc/skinny")).settings(
  coreSettings ++ Seq(
    name := "scala-ddd-base-skinny",
    libraryDependencies ++= Seq(
      "io.monix"             %% "monix"      % "3.0.0-RC1",
      "org.skinny-framework" %% "skinny-orm" % "2.6.0"
    )
  )
) dependsOn core

lazy val redis = (project in file("redis")).settings(
  coreSettings ++ Seq(
    name := "scala-ddd-base-redis",
    libraryDependencies ++= Seq(
      "io.monix"          %% "monix"               % "3.0.0-RC1",
      "com.github.j5ik2o" %% "reactive-redis-core" % "1.0.10"
    )
  )
) dependsOn core

lazy val flyway = (project in file("flyway"))
  .settings(coreSettings)
  .settings(
    libraryDependencies ++= Seq(
      "mysql" % "mysql-connector-java" % "5.1.42"
    ),
    parallelExecution in Test := false,
    wixMySQLVersion := com.wix.mysql.distribution.Version.v5_6_21,
    wixMySQLUserName := Some(dbUser),
    wixMySQLPassword := Some(dbPassword),
    wixMySQLSchemaName := dbName,
    wixMySQLPort := Some(dbPort),
    wixMySQLDownloadPath := Some(sys.env("HOME") + "/.wixMySQL/downloads"),
    wixMySQLTimeout := Some(2 minutes),
    flywayDriver := dbDriver,
    flywayUrl := dbUrl,
    flywayUser := dbUser,
    flywayPassword := dbPassword,
    flywaySchemas := Seq(dbName),
    flywayLocations := Seq(
      s"filesystem:${baseDirectory.value}/src/test/resources/rdb-migration/"
    ),
    flywayMigrate := (flywayMigrate dependsOn wixMySQLStart).value
  )
  .enablePlugins(FlywayPlugin)

lazy val example = (project in file("example"))
  .settings(
    coreSettings ++ Seq(
      name := "scala-ddd-base-example",
      // JDBCのドライバークラス名を指定します(必須)
      driverClassName in generator := dbDriver,
      // JDBCの接続URLを指定します(必須)
      jdbcUrl in generator := dbUrl,
      // JDBCの接続ユーザ名を指定します(必須)
      jdbcUser in generator := dbUser,
      // JDBCの接続ユーザのパスワードを指定します(必須)
      jdbcPassword in generator := dbPassword,
      // カラム型名をどのクラスにマッピングするかを決める関数を記述します(必須)
      propertyTypeNameMapper in generator := {
        case "INTEGER" | "INT" | "TINYINT"     => "Int"
        case "BIGINT"                          => "Long"
        case "VARCHAR"                         => "String"
        case "BOOLEAN" | "BIT"                 => "Boolean"
        case "DATE" | "TIMESTAMP" | "DATETIME" => "java.time.ZonedDateTime"
        case "DECIMAL"                         => "BigDecimal"
        case "ENUM"                            => "String"
      },
      tableNameFilter in generator := { tableName: String =>
        (tableName.toUpperCase != "SCHEMA_VERSION") && (tableName
          .toUpperCase() != "FLYWAY_SCHEMA_HISTORY") && !tableName.toUpperCase
          .endsWith("ID_SEQUENCE_NUMBER")
      },
      outputDirectoryMapper in generator := {
        case s if s.endsWith("Spec") => (sourceDirectory in Test).value
        case s =>
          new java.io.File((scalaSource in Compile).value, "/com/github/j5ik2o/dddbase/example/dao")
      },
      // モデル名に対してどのテンプレートを利用するか指定できます。
      templateNameMapper in generator := {
        case className if className.endsWith("Spec") => "template_spec.ftl"
        case _                                       => "template.ftl"
      },
      generateAll in generator := Def
        .taskDyn {
          val ga = (generateAll in generator).value
          Def
            .task {
              (wixMySQLStop in flyway).value
            }
            .map(_ => ga)
        }
        .dependsOn(flywayMigrate in flyway)
        .value,
      compile in Compile := ((compile in Compile) dependsOn (generateAll in generator)).value,
      libraryDependencies ++= Seq(
        "io.circe"          %% "circe-core"          % circeVersion,
        "io.circe"          %% "circe-generic"       % circeVersion,
        "io.circe"          %% "circe-parser"        % circeVersion,
        "com.github.j5ik2o" %% "reactive-redis-test" % "1.0.10" % Test,
        "com.typesafe.akka" %% "akka-testkit"        % akkaVersion % Test
      )
    )
  )
  .dependsOn(core, slick, skinny, redis, flyway)
  .disablePlugins(WixMySQLPlugin)

lazy val `root` = (project in file("."))
  .settings(coreSettings)
  .settings(
    name := "scala-ddd-base-project"
  )
  .aggregate(core, slick, skinny, example)
