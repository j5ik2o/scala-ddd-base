package com.github.j5ik2o.scala.ddd.functional.skinnyorm

import java.util.concurrent.Executors

import com.github.j5ik2o.scala.ddd.functional.slick.test.FlywayWithMySQLSpecSupport
import org.scalatest.{ BeforeAndAfter, BeforeAndAfterAll, TestSuite }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{ Millis, Seconds, Span }
import scalikejdbc._

import scala.concurrent.ExecutionContext

trait SkinnyORMSpecSupport extends BeforeAndAfter with BeforeAndAfterAll with ScalaFutures {
  this: TestSuite with FlywayWithMySQLSpecSupport =>

  override implicit def patienceConfig: PatienceConfig =
    PatienceConfig(timeout = scaled(Span(5, Seconds)), interval = scaled(Span(15, Millis)))

  GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
    enabled = true,
    logLevel = 'DEBUG,
    warningEnabled = true,
    warningThresholdMillis = 1000L,
    warningLogLevel = 'WARN
  )

  val executor    = Executors.newCachedThreadPool()
  implicit val ec = ExecutionContext.fromExecutor(executor)

  val tables: Seq[String]

  after {
    tables.foreach { table =>
      DB.autoCommit { implicit session =>
        SQL("TRUNCATE TABLE " + table).execute.apply()
      }
    }
  }

  override protected def beforeAll(): Unit = {
    super.beforeAll()
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

  override protected def afterAll(): Unit = {
    ConnectionPool.close()
    super.afterAll()
  }

}
