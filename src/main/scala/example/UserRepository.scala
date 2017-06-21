package example

import cats.data.State
import cats.free.Free
import cats.~>
import com.github.j5ik2o.scala.ddd.functional.{AggregateRepositoryDSL}
import com.github.j5ik2o.scala.ddd.functional.AggregateRepositoryF.{Delete, ResolveById, Store}
import example.db.UserDaoComponent
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class UserRepository {


  def store(aggregate: User): Free[AggregateRepositoryDSL, Unit] = Free.liftF[AggregateRepositoryDSL, Unit](Store(aggregate))

  def resolveBy(id: UserId): Free[AggregateRepositoryDSL, Option[User]] = Free.liftF[AggregateRepositoryDSL, Option[User]](ResolveById(id))

  def deleteBy(id: UserId): Free[AggregateRepositoryDSL, Unit]  = Free.liftF[AggregateRepositoryDSL, Unit](Delete(id))

}

class UserRepositoryInterpreter(val profile: JdbcProfile,val db: JdbcProfile#Backend#Database) extends UserDaoComponent {

  import profile.api._

  type StateAction[A] = State[List[DBIO[Any]], A]

  protected def convertToRecord(aggregate: User): UserRecord = UserRecord(aggregate.id.map(_.value), aggregate.name)
  protected def convertToAggregate(record: UserRecord): User = User(record.id.map(UserId), record.name)

  private def step(implicit ec: ExecutionContext) = new (AggregateRepositoryDSL ~> StateAction) {
    override def apply[A](fa: AggregateRepositoryDSL[A]): StateAction[A] = fa match {
      case ResolveById(id) =>
        val action = UserDao.filter(_.id === id.value.asInstanceOf[Long]).result.headOption.map(_.map(convertToAggregate))
        State.modify[List[DBIO[Any]]](_ :+ action.asInstanceOf[DBIO[A]]).map(_.asInstanceOf[A])
      case Store(aggregate) =>
        val action = UserDao.insertOrUpdate(convertToRecord(aggregate.asInstanceOf[User]))
        State.modify[List[DBIO[Any]]](_ :+ action.asInstanceOf[DBIO[A]]).map(_.asInstanceOf[A])
      case Delete(id) =>
        val action = UserDao.filter(_.id === id.value.asInstanceOf[Long]).delete
        State.modify[List[DBIO[Any]]](_ :+ action.asInstanceOf[DBIO[A]]).map(_.asInstanceOf[A])
    }
  }

  def run[A](program: Free[AggregateRepositoryDSL, A])(implicit ec: ExecutionContext): Future[A] = {
    val (dbio, _) = program.foldMap(step).run(List.empty).value
    val dbioSeq = DBIO.sequence(dbio)
    db.run(dbioSeq).asInstanceOf[Future[A]]
  }

}
