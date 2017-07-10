package com.github.j5ik2o.scala.ddd.functional.skinnyorm

import com.github.j5ik2o.scala.ddd.functional.slick.test.FlywayWithMySQLSpecSupport
import org.scalatest.{ BeforeAndAfter, BeforeAndAfterAll, TestSuite }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{ Millis, Seconds, Span }
import scalikejdbc.ConnectionPool

trait SkinnyORMSpecSupport extends BeforeAndAfter with BeforeAndAfterAll with ScalaFutures {
  this: TestSuite with FlywayWithMySQLSpecSupport =>

  override implicit def patienceConfig: PatienceConfig =
    PatienceConfig(timeout = scaled(Span(5, Seconds)), interval = scaled(Span(15, Millis)))

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
