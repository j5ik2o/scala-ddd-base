package com.github.j5ik2o.dddbase.example.repository.util

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Suite}
import scalikejdbc.config.DBs
import scalikejdbc.{ConnectionPool, GlobalSettings, LoggingSQLAndTimeSettings}

trait SkinnySpecSupport extends BeforeAndAfter with BeforeAndAfterAll with JdbcSpecSupport {
  self: Suite with FlywayWithMySQLSpecSupport =>

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    Class.forName("com.mysql.jdbc.Driver")
    ConnectionPool.singleton(s"jdbc:mysql://localhost:${jdbcPort}/dddbase?useSSL=false", "dddbase", "dddbase")
    GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
      enabled = true,
      logLevel = 'DEBUG,
      warningEnabled = true,
      warningThresholdMillis = 1000L,
      warningLogLevel = 'WARN
    )
  }

  override protected def afterAll(): Unit = {
    DBs.closeAll()
    super.afterAll()
  }

}
