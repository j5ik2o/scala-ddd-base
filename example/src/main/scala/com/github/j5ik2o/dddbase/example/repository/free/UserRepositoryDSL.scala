package com.github.j5ik2o.dddbase.example.repository.free

import com.github.j5ik2o.dddbase.example.model.{UserAccount, UserAccountId}

sealed trait UserRepositoryDSL[A]

case class ResolveMulti(ids: Seq[UserAccountId])      extends UserRepositoryDSL[Seq[UserAccount]]
case class ResolveById(ids: UserAccountId)            extends UserRepositoryDSL[UserAccount]
case class Store(userAccount: UserAccount)            extends UserRepositoryDSL[Long]
case class StoreMulti(userAccounts: Seq[UserAccount]) extends UserRepositoryDSL[Long]
case class SoftDelete(id: UserAccountId)              extends UserRepositoryDSL[Long]
