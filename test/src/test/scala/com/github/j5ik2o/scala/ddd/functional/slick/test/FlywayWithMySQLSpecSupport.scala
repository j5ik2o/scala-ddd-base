package com.github.j5ik2o.scala.ddd.functional.slick.test

import java.io.File

import com.github.j5ik2o.scalatestplus.db._
import org.scalatest.TestSuite

trait FlywayWithMySQLSpecSupport extends FlywayWithMySQLdOneInstancePerSuite with RandomPortSupport {
  this: TestSuite =>

  override protected lazy val mySQLdConfig: MySQLdConfig = MySQLdConfig(
    port = Some(temporaryServerPort()),
    userWithPassword = Some(UserWithPassword("free", "passwd"))
  )

  override protected lazy val downloadConfig: DownloadConfig =
    super.downloadConfig.copy(cacheDir = new File(sys.env("HOME") + "/.wixMySQL/downloads"))

  override protected lazy val schemaConfigs: Seq[SchemaConfig] = Seq(SchemaConfig(name = "free"))

  override protected def flywayConfig(jdbcUrl: String): FlywayConfig =
    FlywayConfig(
      locations = Seq("db/migration/default"),
      placeholderConfig =
        Some(PlaceholderConfig(placeholderReplacement = true, placeholders = Map("engineName" -> "MEMORY")))
    )

}
