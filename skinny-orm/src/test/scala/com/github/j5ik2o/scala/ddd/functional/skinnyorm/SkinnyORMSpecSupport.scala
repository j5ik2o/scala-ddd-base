package com.github.j5ik2o.scala.ddd.functional.skinnyorm

import com.github.j5ik2o.scala.ddd.functional.slick.test.FlywayWithMySQLSpecSupport
import org.scalatest.TestSuite
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{ Millis, Seconds, Span }
import scalikejdbc._

trait SkinnyORMSpecSupport extends ScalaFutures { this: TestSuite with FlywayWithMySQLSpecSupport =>

  override implicit def patienceConfig: PatienceConfig =
    PatienceConfig(timeout = scaled(Span(5, Seconds)), interval = scaled(Span(15, Millis)))

  GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
    enabled = true,
    logLevel = 'DEBUG,
    warningEnabled = true,
    warningThresholdMillis = 1000L,
    warningLogLevel = 'WARN
  )

  def startSkinnyORM(): Unit = {
    Class.forName("com.mysql.jdbc.Driver")
    val jdbcUrl  = mySQLdContext.jdbUrls.head
    val userName = mySQLdContext.userName
    val password = mySQLdContext.password
    ConnectionPool.singleton(
      jdbcUrl,
      userName,
      password
    )
  }

  def stopSkinnyORM(): Unit = {
    ConnectionPool.close()
  }

}
