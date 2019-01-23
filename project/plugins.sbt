addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.15")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.7")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.3")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")

addSbtPlugin("com.chatwork" % "sbt-wix-embedded-mysql" % "1.0.9")

addSbtPlugin("jp.co.septeni-original" % "sbt-dao-generator" % "1.0.8")

addSbtPlugin("io.github.davidmweber" % "flyway-sbt" % "5.0.0")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.4")

resolvers ++= Seq("Seasar2 Repository" at "http://maven.seasar.org/maven2")

libraryDependencies ++= Seq(
  "org.seasar.util" % "s2util" % "0.0.1"
)
