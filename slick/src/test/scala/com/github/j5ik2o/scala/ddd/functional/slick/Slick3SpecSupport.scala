package com.github.j5ik2o.scala.ddd.functional.slick

import com.github.j5ik2o.scala.ddd.functional.slick.test.FlywayWithMySQLSpecSupport
import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{ Millis, Seconds, Span }
import org.scalatest.{ BeforeAndAfter, BeforeAndAfterAll, TestSuite }
import slick.basic.DatabaseConfig
import slick.jdbc.SetParameter.SetUnit
import slick.jdbc.{ JdbcProfile, SQLActionBuilder }

import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Future }

trait Slick3SpecSupport extends BeforeAndAfter with BeforeAndAfterAll with ScalaFutures {
  this: TestSuite with FlywayWithMySQLSpecSupport =>

  override implicit def patienceConfig: PatienceConfig =
    PatienceConfig(timeout = scaled(Span(5, Seconds)), interval = scaled(Span(15, Millis)))

  private var _dbConfig: DatabaseConfig[JdbcProfile] = _

  private var _profile: JdbcProfile = _

  def jdbcPort: Int = mySQLdConfig.port.get

  val tables: Seq[String]

  protected def dbConfig = _dbConfig

  protected def profile = _profile

  after {
    implicit val ec = dbConfig.db.executor.executionContext
    val futures = tables.map { table =>
      val q = SQLActionBuilder(List(s"TRUNCATE TABLE $table"), SetUnit).asUpdate
      dbConfig.db.run(q)
    }
    Await.result(Future.sequence(futures), Duration.Inf)
  }

  override protected def beforeAll(): Unit = {
    super.beforeAll()
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
  }

  override protected def afterAll(): Unit = {
    dbConfig.db.shutdown
    super.afterAll()
  }

}
