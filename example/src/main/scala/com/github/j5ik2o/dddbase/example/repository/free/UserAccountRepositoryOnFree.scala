package com.github.j5ik2o.dddbase.example.repository.free

import cats.free.Free.liftF
import cats.{~>, Monad}
import com.github.j5ik2o.dddbase.example.model.{UserAccount, UserAccountId}
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository.ByFree

object UserAccountRepositoryOnFree extends UserAccountRepository[ByFree] {

  override def resolveById(id: UserAccountId): ByFree[UserAccount] = liftF(ResolveById(id))

  override def resolveMulti(ids: Seq[UserAccountId]): ByFree[Seq[UserAccount]] = liftF(ResolveMulti(ids))

  override def store(aggregate: UserAccount): ByFree[Long] = liftF(Store(aggregate))

  override def storeMulti(aggregates: Seq[UserAccount]): ByFree[Long] = liftF(StoreMulti(aggregates))

  override def softDelete(id: UserAccountId): ByFree[Long] = liftF(SoftDelete(id))

  private def interpreter[M[_]](repo: UserAccountRepository[M]): UserRepositoryDSL ~> M = new (UserRepositoryDSL ~> M) {
    override def apply[A](fa: UserRepositoryDSL[A]): M[A] = fa match {
      case ResolveById(id) =>
        repo.resolveById(id).asInstanceOf[M[A]]
      case ResolveMulti(ids) =>
        repo.resolveMulti(ids).asInstanceOf[M[A]]
      case Store(aggregate) =>
        repo.store(aggregate).asInstanceOf[M[A]]
      case StoreMulti(aggregates) =>
        repo.storeMulti(aggregates).asInstanceOf[M[A]]
      case SoftDelete(id) =>
        repo.softDelete(id).asInstanceOf[M[A]]
    }
  }

  def evaluate[M[_]: Monad, A](evaluator: UserAccountRepository[M])(program: ByFree[A]): M[A] =
    program.foldMap(interpreter(evaluator))

}
