# scala-ddd-base

[![CircleCI](https://circleci.com/gh/j5ik2o/scala-ddd-base.svg?style=svg)](https://circleci.com/gh/j5ik2o/scala-ddd-base)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.j5ik2o/scala-ddd-base_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.j5ik2o/scala-ddd-base_2.12)
[![License: MIT](http://img.shields.io/badge/license-MIT-orange.svg)](LICENSE)

`scala-ddd-base` is provide traits to support for ddd repositories and aggregates.

## Installation

Add the following to your sbt build (Scala 2.11.x, 2.12.x):

### Release Version

```scala
resolvers += "Sonatype OSS Release Repository" at "https://oss.sonatype.org/content/repositories/releases/"

libraryDependencies ++= Seq(
  "com.github.j5ik2o" %% "scala-ddd-base-core" % "1.0.6",
  "com.github.j5ik2o" %% "scala-ddd-base-slick" % "1.0.6"
  // "com.github.j5ik2o" %% "scala-ddd-base-skinny" % "1.0.6"
  // "com.github.j5ik2o" %% "scala-ddd-base-redis" % "1.0.6" 
)
```

### Snapshot Version

```scala
resolvers += "Sonatype OSS Snapshot Repository" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "com.github.j5ik2o" %% "scala-ddd-base-core" % "1.0.6-SNAPSHOT",
  "com.github.j5ik2o" %% "scala-ddd-base-slick" % "1.0.6-SNAPSHOT"
  // "com.github.j5ik2o" %% "scala-ddd-base-skinny" % "1.0.6-SNAPSHOT"
  // "com.github.j5ik2o" %% "scala-ddd-base-redis" % "1.0.6-SNAPSHOT" 
)
```

## Core traits

The following provides basic abstract methods.

- AggregateSingleReader
- AggregateSingleWriter
- AggregateMultiReader
- AggregateMultiWriter
- AggregateSingleDeletable
- AggregateMultiDeletable

## Support traits

The following provides an implementation for each ORM.

### Slick

- AggregateSingleReadFeature
- AggregateSingleWriteFeature
- AggregateMultiReadFeature
- AggregateMultiWriteFeature
- AggregateSingleDeleteFeature

### SkinnyORM

- AggregateSingleReadFeature
- AggregateSingleWriteFeature
- AggregateMultiReadFeature
- AggregateMultiWriteFeature
- AggregateSingleDeleteFeature


## Example

Please mix in the core and support traits to your implementation. 
Slick, SkinnyORM, etc. You can also choose the implementation as you like.

```scala
trait UserAccountRepository[M[_]]
    extends AggregateSingleReader[M]
    with AggregateMultiReader[M]
    with AggregateSingleWriter[M]
    with AggregateMultiWriter[M]
    with AggregateSingleSoftDeletable[M] {
  override type IdType        = UserAccountId
  override type AggregateType = UserAccount
}

object UserAccountRepository {

  type BySlickWithTask[A]  = Task[A]
  type BySkinnyWithTask[A] = ReaderT[Task, DBSession, A]
  type ByFree[A]   = Free[UserRepositoryDSL, A]

  def bySlickWithTask(profile: JdbcProfile, db: JdbcProfile#Backend#Database): UserAccountRepository[BySlickWithTask] =
    new UserAccountRepositoryOnSlick(profile, db)

  def bySkinnyWithTask: UserAccountRepository[BySkinnyWithTask] = new UserAccountRepositoryOnSkinny

  implicit val skinny: UserAccountRepository[BySkinnyWithTask] = bySkinnyWithTask

  implicit val free: UserAccountRepository[ByFree] = UserAccountRepositoryOnFree

  def apply[M[_]](implicit F: UserAccountRepository[M]): UserAccountRepository[M] = F

}
```

- Slick with Task pattern

```scala
class UserAccountRepositoryBySlickWithTask(val profile: JdbcProfile, val db: JdbcProfile#Backend#Database)
    extends UserAccountRepository[UserAccountRepository.BySlickWithTask]
    with AggregateSingleReadFeature
    with AggregateMultiReadFeature
    with AggregateSingleWriteFeature
    with AggregateMultiWriteFeature
    with AggregateSingleSoftDeleteFeature
    with UserAccountComponent {

  override type RecordType = UserAccountRecord
  override type TableType  = UserAccounts
  override protected val dao = UserAccountDao

  override protected def convertToAggregate(record: UserAccountRecord): RIO[UserAccount] =
    Task.pure {
      UserAccount(
        id = UserAccountId(record.id),
        status = Status.withName(record.status),
        emailAddress = EmailAddress(record.email),
        password = HashedPassword(record.password),
        firstName = record.firstName,
        lastName = record.lastName,
        createdAt = record.createdAt,
        updatedAt = record.updatedAt
      )
    }

  override protected def convertToRecord(aggregate: UserAccount): RIO[UserAccountRecord] =
    Task.pure {
      UserAccountRecord(
        id = aggregate.id.value,
        status = aggregate.status.entryName,
        email = aggregate.emailAddress.value,
        password = aggregate.password.value,
        firstName = aggregate.firstName,
        lastName = aggregate.lastName,
        createdAt = aggregate.createdAt,
        updatedAt = aggregate.updatedAt
      )
    }

}
```

- SkinnyORM with Task pattern

```scala
class UserAccountRepositoryBySkinnyWithTask
    extends UserAccountRepository[BySkinnyWithTask]
    with AggregateSingleReadFeature
    with AggregateMultiReadFeature
    with AggregateSingleWriteFeature
    with AggregateMultiWriteFeature
    with AggregateSingleSoftDeleteFeature
    with UserAccountComponent {

  override type RecordType = UserAccountRecord
  override type DaoType    = UserAccountDao.type
  override protected val dao: UserAccountDao.type = UserAccountDao

  override protected def convertToAggregate(record: UserAccountRecord): RIO[UserAccount] =
    ReaderT { _ =>
      Task.pure {
        UserAccount(
          id = UserAccountId(record.id),
          status = Status.withName(record.status),
          emailAddress = EmailAddress(record.email),
          password = HashedPassword(record.password),
          firstName = record.firstName,
          lastName = record.lastName,
          createdAt = record.createdAt,
          updatedAt = record.updatedAt
        )
      }
    }

  override protected def convertToRecord(aggregate: UserAccount): RIO[UserAccountRecord] =
    ReaderT { _ =>
      Task.pure {
        UserAccountRecord(
          id = aggregate.id.value,
          status = aggregate.status.entryName,
          email = aggregate.emailAddress.value,
          password = aggregate.password.value,
          firstName = aggregate.firstName,
          lastName = aggregate.lastName,
          createdAt = aggregate.createdAt,
          updatedAt = aggregate.updatedAt
        )
      }
    }
}
```

- Free monad pattern

```scala
object UserAccountRepositoryByFree extends UserAccountRepository[ByFree] {

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
```


## Usage

- Slick with Task pattern

```scala
val userAccountRepository: UserAccountRepository[BySlickWithTask] = UserAccountRepository.bySlickWithTask(dbConfig.profile, dbConfig.db)
val resultTask: Task[UserAccount] = for {
  _ <- userAccountRepository.store(userAccount)
  result <- userAccountRepository.resolveBy(userAccount.id)
} yield result
val resultFuture: Future[UserAccount] = resultTask.runAsync
```

- Skinny with Task pattern

```scala
val userAccountRepository: UserAccountRepository[BySkinnyWithTask] = UserAccountRepository.bySkinnyWithTask
val resultTask: Task[UserAccount] = for {
  _ <- userAccountRepository.store(userAccount)
  result <- userAccountRepository.resolveBy(userAccount.id)
} yield result
val resultFuture: Future[UserAccount] = resultTask.run(AutoSession).runAsync
```

- Free Monad pattern

```scala
val free: UserAccountRepository[ByFree] = UserAccountRepository[ByFree]
val program: Free[UserRepositoryDSL, UserAccount] = for {
  _      <- free.store(userAccount)
  result <- free.resolveById(userAccount.id)
} yield result

val slick = UserAccountRepository.bySlickWithTask(dbConfig.profile, dbConfig.db)
val resultTask: Task[UserAccount] = UserAccountRepositoryOnFree.evaluate(slick)(program)
val resultFuture: Future[UserAccount] = evalResult.runAsync

// if evaluation by skinny 
// val skinny     = UserAccountRepository.bySkinnyWithTask
// val resultTask: Task[UserAccount] = UserAccountRepositoryOnFree.evaluate(skinny)(program)
// val resultFuture: Future[UserAccount] = evalResult.run(AutoSession).runAsync
```
