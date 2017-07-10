package com.github.j5ik2o.scala.ddd.functional.example.domain.slick

import com.github.j5ik2o.scala.ddd.functional.example.domain.{ User, UserId }
import com.github.j5ik2o.scala.ddd.functional.example.driver.slick3.UserSlickDBIOStorageDriver
import com.github.j5ik2o.scala.ddd.functional.slick.{ CatsDBIOImplicits, Slick3SpecSupport, SlickFutureIOContext }
import com.github.j5ik2o.scala.ddd.functional.slick.test.FlywayWithMySQLSpecSupport
import org.scalatest.FreeSpec
import org.scalatest.concurrent.ScalaFutures

class UserRepositoryDBIOOnSlickSpec
    extends FreeSpec
    with Slick3SpecSupport
    with FlywayWithMySQLSpecSupport
    with ScalaFutures {
  override val tables: Seq[String] = Seq("users")

  "UserRepositoryDBIOOnSlick" - {
    "should be able to store and resolve, when DBIO" in {
      val driver    = new UserSlickDBIOStorageDriver(dbConfig.profile, dbConfig.db)
      val implicits = CatsDBIOImplicits(driver.profile)
      import implicits._
      implicit val ctx = SlickFutureIOContext(ec)
      val repository   = new UserRepositoryDBIOOnSlick(driver)
      val program = for {
        _  <- repository.store(User(UserId(1), "kato"))
        r1 <- repository.resolveBy(UserId(1))
        _  <- repository.deleteById(UserId(1))
        r2 <- repository.resolveBy(UserId(1))
      } yield (r1, r2)
      val dbio   = repository.run(program)
      val future = driver.db.run(dbio)
      val result = future.futureValue
      println(result)
    }
  }

}
