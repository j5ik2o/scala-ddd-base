package example

import org.scalatest.FunSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserRepositorySpec extends FunSpec with FlywayWithMySQLSpecSupport with Slick3SpecSupport {
  override val tables: Seq[String] = Seq("users")

  describe("UserRepository") {
    it("should be able to store & resolve & delete") {
      val repository = new UserRepository(dbConfig.profile, dbConfig.db)
      import repository.profile.api._
      val user1 = User(UserId(1L), "kato")
      val user2 = User(UserId(2L), "junichi")

      val program1 = for {
        _       <- repository.store(user1)
        result1 <- repository.resolveBy(user1.id)
        _       <- repository.deleteBy(user1.id)
        result2 <- repository.resolveBy(user1.id)
      } yield (result1, result2)

      val program2 = for {
        _       <- repository.store(user2)
        result1 <- repository.resolveBy(user2.id)
        _       <- repository.deleteBy(user2.id)
        result2 <- repository.resolveBy(user2.id)
      } yield (result1, result2)

      val dbIO1: repository.DBIOA[(Option[User], Option[User])] = repository.eval(program1)
      val dbIO2: repository.DBIOA[(Option[User], Option[User])] = repository.eval(program2)

      val f: Future[Seq[(Option[User], Option[User])]] =
        repository.db.run(DBIO.sequence(Seq(dbIO1, dbIO2)).transactionally)

      val result = f.futureValue
      println(result)
      assert(result.contains((Some(user1), None)))
      assert(result.contains((Some(user2), None)))
    }
  }
}
