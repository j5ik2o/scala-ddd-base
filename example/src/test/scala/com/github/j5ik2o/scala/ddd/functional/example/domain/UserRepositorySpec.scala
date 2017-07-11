package com.github.j5ik2o.scala.ddd.functional.example.domain

import cats.implicits._
import com.github.j5ik2o.scala.ddd.functional.example.domain.skinnyorm.UserSkinnyORMFutureEvaluator
import com.github.j5ik2o.scala.ddd.functional.example.domain.slick.{ UserSlickDBIOEvaluator, UserSlickFutureEvaluator }
import com.github.j5ik2o.scala.ddd.functional.example.driver.skinnyorm.UserSkinnyORMFutureStorageDriver
import com.github.j5ik2o.scala.ddd.functional.example.driver.slick3.{
  UserSlickDBIOStorageDriver,
  UserSlickFutureStorageDriver
}
import com.github.j5ik2o.scala.ddd.functional.skinnyorm.{ SkinnyORMFutureIOContext, SkinnyORMSpecSupport }
import com.github.j5ik2o.scala.ddd.functional.slick.test.FlywayWithMySQLSpecSupport
import com.github.j5ik2o.scala.ddd.functional.slick.{ CatsDBIOImplicits, Slick3SpecSupport, SlickFutureIOContext }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{ BeforeAndAfterAll, FreeSpec }
import scalikejdbc.AutoSession

class UserRepositorySpec
    extends FreeSpec
    with BeforeAndAfterAll
    with SkinnyORMSpecSupport
    with Slick3SpecSupport
    with FlywayWithMySQLSpecSupport
    with ScalaFutures {

  "UserRepository" - {
    "should be able to store and resolve" - {
      "when DBIO of Slick" in {
        val driver       = UserSlickDBIOStorageDriver(dbConfig.profile, dbConfig.db)
        val evaluator    = UserSlickDBIOEvaluator(driver)
        implicit val ctx = SlickFutureIOContext(ec)
        val program = for {
          _  <- UserRepository.store(User(UserId(1), "kato"))
          r1 <- UserRepository.resolveBy(UserId(1))
          _  <- UserRepository.deleteById(UserId(1))
          r2 <- UserRepository.resolveBy(UserId(1))
        } yield (r1, r2)
        val implicits = CatsDBIOImplicits(driver.profile)
        import implicits._
        val dbio   = evaluator.run(program)
        val future = driver.db.run(dbio)
        val result = future.futureValue
        println(result)
      }
      "when Future of Slick" in {
        val driver    = UserSlickFutureStorageDriver(dbConfig.profile, dbConfig.db)
        val evaluator = UserSlickFutureEvaluator(driver)
        val program = for {
          _  <- UserRepository.store(User(UserId(2), "kato"))
          r1 <- UserRepository.resolveBy(UserId(2))
          _  <- UserRepository.deleteById(UserId(2))
          r2 <- UserRepository.resolveBy(UserId(2))
        } yield (r1, r2)
        implicit val ctx = SlickFutureIOContext(ec)
        val future       = evaluator.run(program)
        val result       = future.futureValue
        println(result)
      }
      "when Future of Skinny" in {
        val driver    = UserSkinnyORMFutureStorageDriver()
        val evaluator = UserSkinnyORMFutureEvaluator(driver)
        val program = for {
          _  <- UserRepository.store(User(UserId(3), "kato"))
          r1 <- UserRepository.resolveBy(UserId(3))
          _  <- UserRepository.deleteById(UserId(3))
          r2 <- UserRepository.resolveBy(UserId(3))
        } yield (r1, r2)
        implicit val ctx = SkinnyORMFutureIOContext(ec, AutoSession)
        val future       = evaluator.run(program)
        val result       = future.futureValue
        println(result)
      }
    }
  }

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    startSlick()
    startSkinnyORM()
  }

  override protected def afterAll(): Unit = {
    stopSkinnyORM()
    stopSlick()
    super.afterAll()
  }
}
