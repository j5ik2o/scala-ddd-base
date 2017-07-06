package example

import org.scalatest.FunSpec
import scala.concurrent.ExecutionContext.Implicits.global

class UserRepositorySpec extends FunSpec with FlywayWithMySQLSpecSupport with Slick3SpecSupport {
  override val tables: Seq[String] = Seq("users")

  describe("UserRepository") {
    it("should be able to store & resolve & delete") {
      val user       = User(UserId(1L), "kato")
      val repository = new UserRepository(dbConfig.profile, dbConfig.db)
      val program = for {
        _       <- repository.store(user)
        result1 <- repository.resolveBy(user.id)
        _       <- repository.deleteBy(user.id)
        result2 <- repository.resolveBy(user.id)
      } yield (result1, result2)
      val f      = repository.run(program)
      val result = f.futureValue
      println(result)
      assert(result._1.nonEmpty)
      assert(result._1.get === user)
      assert(result._2.isEmpty)
    }
  }
}
