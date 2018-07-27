import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._
import sbtrelease._
import Utils._

releaseCrossBuild := true

releasePublishArtifactsAction := PgpKeys.publishSigned.value

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+core/publishSigned"),
  releaseStepCommandAndRemaining("+slick/publishSigned"),
  releaseStepCommandAndRemaining("+skinny/publishSigned"),
  releaseStepCommandAndRemaining("+redis/publishSigned"),
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)
