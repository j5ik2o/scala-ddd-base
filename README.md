# scala-ddd-base

`scala-ddd-base` is provide traits to support for ddd repositories and aggregates.

## Installation

Add the following to your sbt build (Scala 2.12.x):

### Release Version

```scala
resolvers += "Sonatype OSS Release Repository" at "https://oss.sonatype.org/content/repositories/releases/"

libraryDependencies += "com.github.j5ik2o" %% "scala-ddd-base" % "1.0.0"
```

### Snapshot Version

```scala
resolvers += "Sonatype OSS Snapshot Repository" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies += "com.github.j5ik2o" %% "scala-ddd-base" % "1.0.0-SNAPSHOT"
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

### SkinyORM

- AggregateSingleReadFeature
- AggregateSingleWriteFeature
- AggregateMultiReadFeature
- AggregateMultiWriteFeature
- AggregateSingleDeleteFeature


## Example

Please mix in the core and support traits to your implementation.

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
