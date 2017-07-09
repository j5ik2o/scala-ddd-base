package com.github.j5ik2o.scala.ddd.functional.example.domain

import com.github.j5ik2o.scala.ddd.functional.example.slick3.{ UserDBIODriver, UserFutureDriver }
import com.github.j5ik2o.scala.ddd.functional.skinnyorm.SkinnyORMSpecSupport
import com.github.j5ik2o.scala.ddd.functional.slick.{ CatsDBIOMonadInstance, Slick3SpecSupport }
import com.github.j5ik2o.scala.ddd.functional.slick.test.FlywayWithMySQLSpecSupport
import org.scalatest.FreeSpec
import org.scalatest.concurrent.ScalaFutures
import slick.jdbc.JdbcProfile
import cats.implicits._

class UserRepositorySpec
    extends FreeSpec
    with Slick3SpecSupport
    with SkinnyORMSpecSupport
    with FlywayWithMySQLSpecSupport
    with ScalaFutures {

  override val tables: Seq[String] = Seq("users")

  "UserRepository" - {
    "should be able to store and resolve, when DBIO" in {
      val driver = new UserDBIODriver(dbConfig.profile, dbConfig.db)
      new CatsDBIOMonadInstance {
        override val profile: JdbcProfile = driver.profile
        val repository                    = new UserRepositoryByDBIO(driver)
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
    "should be able to store and resolve, when Future" in {
      val driver     = new UserFutureDriver(dbConfig.profile, dbConfig.db)
      val repository = new UserRepositoryByFuture(driver)
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
