# scala-ddd-base

## Core traits

- AggregateSingleReader
- AggregateSingleWriter
- AggregateMultiReader
- AggregateMultiWriter
- AggregateSingleDeletable
- AggregateMultiDeletable

## Support traits

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
