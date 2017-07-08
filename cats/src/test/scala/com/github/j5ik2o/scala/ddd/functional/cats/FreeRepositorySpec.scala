package com.github.j5ik2o.scala.ddd.functional.cats

import cats.~>
import org.scalatest.FreeSpec

import scala.concurrent.{ ExecutionContext, Future }

class UserRepository() extends FreeRepository {
  override type IdValueType   = Long
  override type IdType        = UserId
  override type AggregateType = User
  override type R[A]          = Future[A]

  override def interpreter(implicit ec: ExecutionContext): AggregateRepositoryDSL ~> Future =
    new (AggregateRepositoryDSL ~> Future) {
      override def apply[A](fa: AggregateRepositoryDSL[A]): Future[A] = fa match {
        case Store(aggregate) =>
        case ResolveById(id)  =>
      }
    }
}

class FreeRepositorySpec extends FreeSpec {

  "" - {}

}
