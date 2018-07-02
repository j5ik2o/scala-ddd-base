import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._
import sbtrelease._
import Utils._

val sonatypeURL = "https://oss.sonatype.org/service/local/repositories/"

val updateReadme = { state: State =>
  val extracted         = Project.extract(state)
  val scalaV            = extracted get scalaBinaryVersion
  val v                 = extracted get version
  val org               = extracted get organization
  val n                 = (extracted get name).replace("-project", "")
  val snapshotOrRelease = if (extracted get isSnapshot) "snapshots" else "releases"
  val readme            = "README.md"
  val readmeFile        = file(readme)
  val newReadme = Predef
    .augmentString(IO.read(readmeFile))
    .lines
    .map { line =>
      val matchReleaseOrSnapshot = line.contains("SNAPSHOT") == v.contains("SNAPSHOT")
      if (line.startsWith("libraryDependencies") && matchReleaseOrSnapshot) {
        s"""libraryDependencies += "${org}" %% "${n}" % "$v""""
      } else line
    }
    .mkString("", "\n", "\n")
  IO.write(readmeFile, newReadme)
  val git = new Git(extracted get baseDirectory)
  git.add(readme) ! state.log.toScalaProcessLogger
  git.commit("update " + readme, false) ! state.log.toScalaProcessLogger
  git.cmd("diff", "HEAD^") ! state.log.toScalaProcessLogger
  state
}

commands += Command.command("updateReadme")(updateReadme)

val updateReadmeProcess: ReleaseStep = updateReadme

releaseCrossBuild := true

releasePublishArtifactsAction := PgpKeys.publishSigned.value

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  updateReadmeProcess,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  setNextVersion,
  commitNextVersion,
  updateReadmeProcess,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)
