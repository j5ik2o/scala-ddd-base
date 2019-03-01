import scala.concurrent.duration._

val scalaVersion211 = "2.11.12"
val scalaVersion212 = "2.12.8"

val reactiveRedisVersion     = "1.0.21"
val reactiveMemcachedVersion = "1.0.6"
val reactiveDynamoDBVersion  = "1.0.10-SNAPSHOT"
val circeVersion             = "0.11.1"
val akkaHttpVersion          = "10.1.7"
val akkaVersion              = "2.5.19"
val slickVersion             = "3.2.3"
val catsVersion              = "1.5.0"
val monixVersion             = "3.0.0-RC2"

val dbDriver    = "com.mysql.jdbc.Driver"
val dbName      = "dddbase"
val dbUser      = "dddbase"
val dbPassword  = "passwd"
val dbPort: Int = Utils.RandomPortSupport.temporaryServerPort()
val dbUrl       = s"jdbc:mysql://localhost:$dbPort/$dbName?useSSL=false"

//val compileScalaStyle = taskKey[Unit]("compileScalaStyle")
//
//lazy val scalaStyleSettings = Seq(
//  (scalastyleConfig in Compile) := file("scalastyle-config.xml"),
//  compileScalaStyle := scalastyle.in(Compile).toTask("").value,
//  (compile in Compile) := (compile in Compile).dependsOn(compileScalaStyle).value
//)

val coreSettings = Seq(
  sonatypeProfileName := "com.github.j5ik2o",
  organization := "com.github.j5ik2o",
  scalaVersion := scalaVersion212,
  crossScalaVersions ++= Seq(scalaVersion211, scalaVersion212),
  scalacOptions ++= {
    Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-encoding",
      "UTF-8",
      "-language:_",
      "-Ydelambdafy:method",
      "-target:jvm-1.8"
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
    Resolver.sonatypeRepo("snapshots"),
    Resolver.sonatypeRepo("releases"),
    "Seasar2 Repository" at "http://maven.seasar.org/maven2",
    Resolver.bintrayRepo("danslapman", "maven"),
    "DynamoDB Local Repository" at "https://s3-us-west-2.amazonaws.com/dynamodb-local/release"
  ),
  libraryDependencies ++= Seq(
    "org.scalatest"     %% "scalatest"        % "3.0.5"  % Test,
    "org.scalacheck"    %% "scalacheck"       % "1.14.0" % Test,
    "ch.qos.logback"    % "logback-classic"   % "1.2.3"  % Test,
    "com.github.j5ik2o" %% "scalatestplus-db" % "1.0.7"  % Test
  ),
  addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.9" cross CrossVersion.binary)
) // ++ scalaStyleSettings

val baseDependencies = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core"  % catsVersion,
    "org.typelevel" %% "cats-free"  % catsVersion,
    "com.beachape"  %% "enumeratum" % "1.5.13",
    "org.slf4j"     % "slf4j-api"   % "1.7.25",
    "io.monix"      %% "monix"      % monixVersion
  )
)

lazy val core = (project in file("core")).settings(
  coreSettings ++ Seq(
    name := "scala-ddd-base-core",
    libraryDependencies ++= Seq()
  )
)

lazy val slick = (project in file("jdbc/slick"))
  .settings(
    coreSettings ++ baseDependencies ++ Seq(
      name := "scala-ddd-base-slick",
      libraryDependencies ++= Seq(
        "com.typesafe.slick" %% "slick"          % slickVersion,
        "com.typesafe.slick" %% "slick-hikaricp" % slickVersion
      )
    )
  )
  .dependsOn(core)
  .disablePlugins(WixMySQLPlugin)

lazy val skinny = (project in file("jdbc/skinny"))
  .settings(
    coreSettings ++ baseDependencies ++ Seq(
      name := "scala-ddd-base-skinny",
      libraryDependencies ++= Seq(
        "org.skinny-framework" %% "skinny-orm" % "2.6.0"
      )
    )
  )
  .dependsOn(core)
  .disablePlugins(WixMySQLPlugin)

lazy val dynamodb = (project in file("nosql/dynamodb"))
  .settings(
    coreSettings ++ Seq(
      name := "scala-ddd-base-dynamodb",
      libraryDependencies ++= Seq(
        "com.github.j5ik2o" %% "reactive-dynamodb-v2-monix" % reactiveDynamoDBVersion
      )
    )
  )
  .dependsOn(core)
  .disablePlugins(WixMySQLPlugin)

lazy val redis = (project in file("nosql/redis"))
  .settings(
    coreSettings ++ Seq(
      name := "scala-ddd-base-redis",
      libraryDependencies ++= Seq(
        "com.github.j5ik2o" %% "reactive-redis-core" % reactiveRedisVersion
      )
    )
  )
  .dependsOn(core)
  .disablePlugins(WixMySQLPlugin)

lazy val memcached = (project in file("nosql/memcached"))
  .settings(
    coreSettings ++ baseDependencies ++ Seq(
      name := "scala-ddd-base-memcached",
      libraryDependencies ++= Seq(
        "com.github.j5ik2o" %% "reactive-memcached-core" % reactiveMemcachedVersion
      )
    )
  )
  .dependsOn(core)
  .disablePlugins(WixMySQLPlugin)

lazy val memory = (project in file("nosql/memory"))
  .settings(
    coreSettings ++ baseDependencies ++ Seq(
      name := "scala-ddd-base-memory",
      libraryDependencies ++= Seq()
    )
  )
  .dependsOn(core)
  .disablePlugins(WixMySQLPlugin)

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
    skip in publish := true,
    flywayMigrate := (flywayMigrate dependsOn wixMySQLStart).value
  )
  .enablePlugins(FlywayPlugin)

lazy val example = (project in file("example"))
  .settings(
    coreSettings ++ baseDependencies ++ Seq(
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
        case className                               => className + "_template.ftl"
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
        "org.wvlet.airframe" %% "airframe"                % "0.64",
        "com.google.guava"   % "guava"                    % "27.0.1-jre",
        "io.circe"           %% "circe-core"              % circeVersion,
        "io.circe"           %% "circe-generic"           % circeVersion,
        "io.circe"           %% "circe-parser"            % circeVersion,
        "com.github.j5ik2o"  %% "reactive-dynamodb-test"  % reactiveDynamoDBVersion % Test,
        "com.github.j5ik2o"  %% "reactive-redis-test"     % reactiveRedisVersion % Test,
        "com.github.j5ik2o"  %% "reactive-memcached-test" % reactiveMemcachedVersion % Test,
        "com.typesafe.akka"  %% "akka-testkit"            % akkaVersion % Test
      ),
      parallelExecution in Test := false,
      skip in publish := true
    )
  )
  .dependsOn(core, slick, skinny, redis, memcached, dynamodb, memory, flyway)
  .disablePlugins(WixMySQLPlugin)

lazy val `root` = (project in file("."))
  .settings(coreSettings)
  .settings(
    name := "scala-ddd-base-project"
  )
  .aggregate(core, slick, skinny, redis, memcached, dynamodb, memory, example)
