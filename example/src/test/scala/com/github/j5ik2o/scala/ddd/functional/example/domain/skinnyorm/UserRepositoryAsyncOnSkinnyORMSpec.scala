package com.github.j5ik2o.scala.ddd.functional.example.domain.skinnyorm

import com.github.j5ik2o.scala.ddd.functional.example.domain.{ User, UserId }
import com.github.j5ik2o.scala.ddd.functional.example.driver.skinnyorm.UserSkinnyORMFutureStorageDriver
import com.github.j5ik2o.scala.ddd.functional.skinnyorm.{ SkinnyORMFutureIOContext, SkinnyORMSpecSupport }
import com.github.j5ik2o.scala.ddd.functional.slick.test.FlywayWithMySQLSpecSupport
import org.scalatest.FreeSpec
import org.scalatest.concurrent.ScalaFutures
import scalikejdbc.AutoSession
import cats.implicits._

class UserRepositoryAsyncOnSkinnyORMSpec
    extends FreeSpec
    with SkinnyORMSpecSupport
    with FlywayWithMySQLSpecSupport
    with ScalaFutures {

  override val tables: Seq[String] = Seq("users")

  "UserRepositoryAsyncOnSkinnyORM" - {
    "should be able to store and resolve" in {
      val driver       = new UserSkinnyORMFutureStorageDriver()
      val repository   = new UserRepositoryAsyncOnSkinnyORM(driver)
      implicit val ctx = SkinnyORMFutureIOContext(ec, AutoSession)
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
