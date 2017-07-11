package com.github.j5ik2o.scala.ddd.functional.slick

import com.github.j5ik2o.scala.ddd.functional.slick.test.FlywayWithMySQLSpecSupport
import com.typesafe.config.ConfigFactory
import org.scalatest.TestSuite
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{ Millis, Seconds, Span }
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

trait Slick3SpecSupport extends ScalaFutures { this: TestSuite with FlywayWithMySQLSpecSupport =>

  override implicit def patienceConfig: PatienceConfig =
    PatienceConfig(timeout = scaled(Span(5, Seconds)), interval = scaled(Span(15, Millis)))

  private var _dbConfig: DatabaseConfig[JdbcProfile] = _

  private var _profile: JdbcProfile = _

  private var _ec: ExecutionContext = _

  def jdbcPort: Int = mySQLdConfig.port.get

  protected def dbConfig = _dbConfig

  protected def profile = _profile

  implicit def ec: ExecutionContext = _ec

  def startSlick(): Unit = {
    val config = ConfigFactory.parseString(s"""
                                              |free {
                                              |  profile = "slick.jdbc.MySQLProfile$$"
                                              |  db {
                                              |    connectionPool = disabled
                                              |    driver = "com.mysql.jdbc.Driver"
                                              |    url = "jdbc:mysql://localhost:$jdbcPort/free?useSSL=false"
                                              |    user = "free"
                                              |    password = "passwd"
                                              |  }
                                              |}
      """.stripMargin)
    _dbConfig = DatabaseConfig.forConfig[JdbcProfile]("free", config)
    _profile = dbConfig.profile
    _ec = dbConfig.db.executor.executionContext
  }

  def stopSlick(): Unit = {
    dbConfig.db.shutdown
  }

}
