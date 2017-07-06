package com.github.j5ik2o.scala.ddd.functional

import cats.free.Free
import cats.~>
import com.github.j5ik2o.scala.ddd.functional.AggregateRepositoryDSL.{ Delete, ResolveById, Store }
import slick.jdbc.JdbcProfile

import scala.concurrent.{ ExecutionContext, Future }

trait Repository extends AggregateIO with DBIOMonadInstance {
  val profile: JdbcProfile
  val db: JdbcProfile#Backend#Database

  import profile.api._

  override type IdValueType
  type RecordType <: DaoRecord
  type TableType <: Table[RecordType] {
    def id: Rep[IdValueType]
    type TableElementType = RecordType
  }

  protected val dao: TableQuery[TableType]

  def store(aggregate: AggregateType): Free[AggregateRepositoryDSL, Unit] =
    Free.liftF[AggregateRepositoryDSL, Unit](Store(aggregate))

  def resolveBy(id: IdType): Free[AggregateRepositoryDSL, Option[AggregateType]] =
    Free.liftF[AggregateRepositoryDSL, Option[AggregateType]](ResolveById(id))

  def deleteBy(id: IdType): Free[AggregateRepositoryDSL, Unit] = Free.liftF[AggregateRepositoryDSL, Unit](Delete(id))

  protected class DBIOInterpreter(implicit ec: ExecutionContext) extends (AggregateRepositoryDSL ~> DBIO) {
    override def apply[A](fa: AggregateRepositoryDSL[A]): DBIO[A] = fa match {
      case ResolveById(id) =>
        findById(id.asInstanceOf[IdType])
      case Store(aggregate) =>
        store(aggregate.asInstanceOf[AggregateType])
      case Delete(id) =>
        deleteById(id.asInstanceOf[IdType])
    }
  }

  protected def convertToRecord(aggregate: AggregateType): RecordType

  protected def convertToAggregate(record: RecordType): AggregateType

  protected def findById[A](id: IdType)(implicit ec: ExecutionContext): DBIO[A] = {
    val action =
      dao.filter(_.id === id.value).result.headOption.map(_.map(convertToAggregate))
    action.asInstanceOf[DBIO[A]]
  }

  protected def store[A](aggregate: AggregateType)(implicit ec: ExecutionContext): DBIO[A] = {
    val record = convertToRecord(aggregate)
    val action = (for {
      n <- dao.filter(_.id === aggregate.id.value).update(record)
      _ <- if (n == 0) dao.forceInsert(record) else DBIO.successful(n)
    } yield ()).transactionally
    action.asInstanceOf[DBIO[A]]
  }

  protected def deleteById[A](id: IdType)(implicit ec: ExecutionContext): DBIO[A] = {
    val action = dao.filter(_.id === id.value).delete
    action
      .flatMap { v =>
        if (v == 1)
          DBIO.successful(())
        else
          DBIO.failed(new Exception())
      }
      .asInstanceOf[DBIO[A]]
  }

  protected def interpreter(implicit ec: ExecutionContext): (AggregateRepositoryDSL ~> DBIO) = new DBIOInterpreter

  def eval[A](program: Free[AggregateRepositoryDSL, A])(implicit ec: ExecutionContext): DBIO[A] =
    program.foldMap(interpreter)

  def run[A](program: Free[AggregateRepositoryDSL, A])(implicit ec: ExecutionContext): Future[A] =
    db.run(eval(program))

}
