package example

import cats.free.Free
import cats.{ ~>, Monad }
import com.github.j5ik2o.scala.ddd.functional.AggregateRepositoryDSL
import com.github.j5ik2o.scala.ddd.functional.AggregateRepositoryF.{ Delete, ResolveById, Store }
import example.db.UserDaoComponent
import slick.jdbc.JdbcProfile

import scala.concurrent.{ ExecutionContext, Future }

class UserRepository {

  def store(aggregate: User): Free[AggregateRepositoryDSL, Unit] =
    Free.liftF[AggregateRepositoryDSL, Unit](Store(aggregate))

  def resolveBy(id: UserId): Free[AggregateRepositoryDSL, Option[User]] =
    Free.liftF[AggregateRepositoryDSL, Option[User]](ResolveById(id))

  def deleteBy(id: UserId): Free[AggregateRepositoryDSL, Unit] = Free.liftF[AggregateRepositoryDSL, Unit](Delete(id))

}

class UserRepositoryInterpreter(val profile: JdbcProfile, val db: JdbcProfile#Backend#Database)
    extends UserDaoComponent {

  import profile.api._

  implicit def dbIOMonad(implicit ec: ExecutionContext) = new Monad[DBIO] {
    override def pure[A](x: A): DBIO[A] = DBIO.successful(x)

    override def flatMap[A, B](fa: DBIO[A])(f: (A) => DBIO[B]): DBIO[B] = fa.flatMap(f)

    override def tailRecM[A, B](a: A)(f: (A) => DBIO[Either[A, B]]): DBIO[B] = f(a).flatMap {
      case Left(a1) => tailRecM(a1)(f)
      case Right(b) => DBIO.successful(b)
    }

  }

  protected def convertToRecord(aggregate: User): UserRecord = UserRecord(aggregate.id.map(_.value), aggregate.name)
  protected def convertToAggregate(record: UserRecord): User = User(record.id.map(UserId), record.name)

  private def step(implicit ec: ExecutionContext) = new (AggregateRepositoryDSL ~> DBIO) {
    override def apply[A](fa: AggregateRepositoryDSL[A]): DBIO[A] = fa match {
      case ResolveById(id) =>
        val action =
          UserDao.filter(_.id === id.value.asInstanceOf[Long]).result.headOption.map(_.map(convertToAggregate))
        action.asInstanceOf[DBIO[A]]
      case Store(aggregate) =>
        val action = UserDao.insertOrUpdate(convertToRecord(aggregate.asInstanceOf[User]))
        action.asInstanceOf[DBIO[A]]
      case Delete(id) =>
        val action = UserDao.filter(_.id === id.value.asInstanceOf[Long]).delete
        action.asInstanceOf[DBIO[A]]
    }
  }

  def run[A](program: Free[AggregateRepositoryDSL, A])(implicit ec: ExecutionContext): Future[A] = {
    val result = program.foldMap(step)
    db.run(result)
  }

}
