package com.github.j5ik2o.scala.ddd.functional.example.domain.slick

import com.github.j5ik2o.scala.ddd.functional.example.domain.{ User, UserId }
import com.github.j5ik2o.scala.ddd.functional.example.driver.slick3.UserSlickFutureDriver
import com.github.j5ik2o.scala.ddd.functional.slick.{ Slick3SpecSupport, SlickFutureIOContext }
import com.github.j5ik2o.scala.ddd.functional.slick.test.FlywayWithMySQLSpecSupport
import org.scalatest.FreeSpec
import org.scalatest.concurrent.ScalaFutures
import cats.implicits._

class UserRepositoryAsyncOnSlickSpec
    extends FreeSpec
    with Slick3SpecSupport
    with FlywayWithMySQLSpecSupport
    with ScalaFutures {
  override val tables: Seq[String] = Seq("users")

  "UserRepositoryAsyncOnSlick" - {
    "should be able to store and resolve" in {
      val driver       = new UserSlickFutureDriver(dbConfig.profile, dbConfig.db)
      val repository   = new UserRepositoryAsyncOnSlick(driver)
      implicit val ctx = SlickFutureIOContext(ec)
      val program = for {
        _  <- repository.store(User(UserId(1), "kato"))
        r1 <- repository.resolveBy(UserId(1))
        _  <- repository.deleteById(UserId(1))
        r2 <- repository.resolveBy(UserId(1))
      } yield (r1, r2)
      val future = repository.run(program)
      val result = future.futureValue
      println(result)
    }
  }
}
