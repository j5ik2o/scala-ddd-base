package com.github.j5ik2o.scala.ddd.functional.example.domain

import com.github.j5ik2o.scala.ddd.functional.example.slick3.UserSlick3Driver
import com.github.j5ik2o.scala.ddd.functional.skinnyorm.SkinnyORMSpecSupport
import com.github.j5ik2o.scala.ddd.functional.slick.Slick3SpecSupport
import com.github.j5ik2o.scala.ddd.functional.slick.test.FlywayWithMySQLSpecSupport
import org.scalatest.FreeSpec
import org.scalatest.concurrent.ScalaFutures

class UserRepositorySpec
    extends FreeSpec
    with Slick3SpecSupport
    with SkinnyORMSpecSupport
    with FlywayWithMySQLSpecSupport
    with ScalaFutures {

  override val tables: Seq[String] = Seq("users")

  "UserRepository" - {
    "when slick3" in {
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
//    "when skinnyorm" in {
//      "should be able to store and resolve" in {
//        val driver     = new UserSkinnyORMDriver()
//        val repository = new UserRepository(driver)
//        val program = for {
//          _  <- repository.store(User(UserId(1), "kato"))
//          r1 <- repository.resolveBy(UserId(1))
//          _  <- repository.deleteById(UserId(1))
//          r2 <- repository.resolveBy(UserId(1))
//        } yield (r1, r2)
//        val future = repository.realize(program)
//        val result = future.futureValue
//        println(result)
//      }
//    }

  }
}
