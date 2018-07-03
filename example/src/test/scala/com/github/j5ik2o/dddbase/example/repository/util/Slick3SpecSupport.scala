package com.github.j5ik2o.dddbase.example.repository.util

import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Suite}
import slick.basic.DatabaseConfig
import slick.jdbc.SetParameter.SetUnit
import slick.jdbc.{JdbcProfile, SQLActionBuilder}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

trait Slick3SpecSupport extends BeforeAndAfter with BeforeAndAfterAll with ScalaFutures with JdbcSpecSupport {
  self: Suite with FlywayWithMySQLSpecSupport =>

  private var _dbConfig: DatabaseConfig[JdbcProfile] = _

  private var _profile: JdbcProfile = _

  protected def dbConfig = _dbConfig

  protected def profile = _profile

  after {
    implicit val ec = dbConfig.db.executor.executionContext
    val futures = tables.map { table =>
      val q = SQLActionBuilder(List(s"TRUNCATE TABLE $table"), SetUnit).asUpdate
      dbConfig.db.run(q)
    }
    Future.sequence(futures).futureValue
  }

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    val config = ConfigFactory.parseString(s"""
         |dddbase {
         |  profile = "slick.jdbc.MySQLProfile$$"
         |  db {
         |    connectionPool = disabled
         |    driver = "com.mysql.jdbc.Driver"
         |    url = "jdbc:mysql://localhost:$jdbcPort/dddbase?useSSL=false"
         |    user = "dddbase"
         |    password = "dddbase"
         |  }
         |}
      """.stripMargin)
    _dbConfig = DatabaseConfig.forConfig[JdbcProfile]("dddbase", config)
    _profile = dbConfig.profile
  }

  override protected def afterAll(): Unit = {
    dbConfig.db.shutdown
    super.afterAll()
  }

}
