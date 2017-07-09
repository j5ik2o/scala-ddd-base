package com.github.j5ik2o.scala.ddd.functional.slick

import org.scalatest.FreeSpec
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.ExecutionContext.Implicits.global

class FreeRepositorySpec extends FreeSpec with FlywayWithMySQLSpecSupport with Slick3SpecSupport with ScalaFutures {

  override val tables: Seq[String] = Seq("users")

  "UserRepository" - {
    "should be able to store and resolve" in {
      val driver     = new UserSlick3Driver(dbConfig.profile, dbConfig.db)
      val repository = new UserRepository(driver)
      val program = for {
        _  <- repository.store(User(UserId(1), "kato"))
        r1 <- repository.resolveBy(UserId(1))
        _  <- repository.deleteById(UserId(1))
        r2 <- repository.resolveBy(UserId(1))
      } yield (r1, r2)
      val future = repository.realize(program)
      val result = future.futureValue
      println(result)
    }

  }
}
