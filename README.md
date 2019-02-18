# scala-ddd-base

[![CircleCI](https://circleci.com/gh/j5ik2o/scala-ddd-base/tree/master.svg?style=shield&circle-token=77d5ba85babad56b6a3fdd0f5be9e140ec12a4ae)](https://circleci.com/gh/j5ik2o/scala-ddd-base/tree/master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.j5ik2o/scala-ddd-base-core_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.j5ik2o/scala-ddd-base-core_2.12)
[![Scaladoc](http://javadoc-badge.appspot.com/com.github.j5ik2o/scala-ddd-base-core_2.12.svg?label=scaladoc)](http://javadoc-badge.appspot.com/com.github.j5ik2o/scala-ddd-base-core_2.12/com/github/j5ik2o/dddbase/index.html?javadocio=true)
[![License: MIT](http://img.shields.io/badge/license-MIT-orange.svg)](LICENSE)

`scala-ddd-base` provides traits to support for ddd repositories and aggregates.

## Installation


Add the following to your sbt build (Scala 2.11.x, 2.12.x):

```scala
resolvers += "Sonatype OSS Release Repository" at "https://oss.sonatype.org/content/repositories/releases/"

val scalaDddBaseVersion = "..."

libraryDependencies ++= Seq(
  "com.github.j5ik2o" %% "scala-ddd-base-core" % scalaDddBaseVersion,
  // Please set as necessary
  // "com.github.j5ik2o" %% "scala-ddd-base-slick" % scalaDddBaseVersion,
  // "com.github.j5ik2o" %% "scala-ddd-base-skinny" % scalaDddBaseVersion,
  // "com.github.j5ik2o" %% "scala-ddd-base-redis" % scalaDddBaseVersion,
  // "com.github.j5ik2o" %% "scala-ddd-base-memcached" % scalaDddBaseVersion,
  // "com.github.j5ik2o" %% "scala-ddd-base-dynamodb" % scalaDddBaseVersion,
  // "com.github.j5ik2o" %% "scala-ddd-base-memory" % scalaDddBaseVersion
)
```

## Core traits

The following provides basic abstract methods.

- AggregateSingleReader
- AggregateSingleWriter
- AggregateMultiReader
- AggregateMultiWriter
- AggregateSingleSoftDeletable
- AggregateSingleHardDeletable
- AggregateMultiSoftDeletable
- AggregateMultiHardDeletable

## Support traits

The following provides an implementation for each ORM/KVS.

- AggregateSingleReadFeature
- AggregateSingleWriteFeature
- AggregateMultiReadFeature
- AggregateMultiWriteFeature
- AggregateSingleSoftDeleteFeature
- AggregateSingleHardDeleteFeature
- AggregateMultiSoftDeleteFeature
- AggregateMultiHardDeleteFeature

The supported ORM/KVS/Cache is below.

- Slick(JDBC)
- SkinnyORM(JDBC)
- Redis([reactive-redis](https://github.com/j5ik2o/reactive-redis))
- Memcached([reactive-memcached](https://github.com/j5ik2o/reactive-memcached))
- DynamoDB([reactive-dynamodb](https://github.com/j5ik2o/reactive-dynamodb))
- Memory([Guava Cache](https://github.com/google/guava))

## Example

Please mix in the core and support traits to your implementation. 
Slick, SkinnyORM, Memcached, Redis, Memory etc. You can also choose the implementation as you like.

```scala
trait UserAccountRepository[M[_]]
    extends AggregateSingleReader[M]
    with AggregateMultiReader[M]
    with AggregateSingleWriter[M]
    with AggregateMultiWriter[M]
    with AggregateSingleSoftDeletable[M]
    with AggregateMultiSoftDeletable[M] {
  override type IdType        = UserAccountId
  override type AggregateType = UserAccount
}

object UserAccountRepository {

  type OnRedis[A]     = ReaderT[Task, RedisConnection, A]
  type OnMemcached[A] = ReaderT[Task, MemcachedConnection, A]
  type OnDynamoDB[A]  = Task[A]
  type OnMemory[A]    = Task[A]
  type BySlick[A]     = Task[A]
  type BySkinny[A]    = ReaderT[Task, DBSession, A]
  type ByFree[A]      = Free[UserRepositoryDSL, A]

  def bySlick(profile: JdbcProfile, db: JdbcProfile#Backend#Database): UserAccountRepository[BySlick] =
    new UserAccountRepositoryBySlick(profile, db)

  def bySkinny: UserAccountRepository[BySkinny] = new UserAccountRepositoryBySkinny

  def onRedis(
      expireDuration: Duration
  )(implicit actorSystem: ActorSystem): UserAccountRepository[OnRedis] =
    new UserAccountRepositoryOnRedis(expireDuration)

  def onMemcached(
      expireDuration: Duration
  )(implicit actorSystem: ActorSystem): UserAccountRepository[OnMemcached] =
    new UserAccountRepositoryOnMemcached(expireDuration)
    
  def onDynamoDB(dynamoDbAsyncClient: DynamoDbAsyncClient): UserAccountRepository[OnDynamoDB] =
    new UserAccountRepositoryOnDynamoDB(DynamoDBTaskClientV2(DynamoDBAsyncClientV2(underlying))

  def onMemory(minSize: Option[Int] = None,
               maxSize: Option[Int] = None,
               expireDuration: Option[Duration] = None,
               concurrencyLevel: Option[Int] = None,
               maxWeight: Option[Int] = None): UserAccountRepository[OnMemory] =
    new UserAccountRepositoryOnMemory(minSize, maxSize, expireDuration, concurrencyLevel, maxWeight)
    
}
```

- [for Slick3](example/src/main/scala/com/github/j5ik2o/dddbase/example/repository/slick/UserAccountRepositoryBySlick.scala)
- [for SkinnyORM](example/src/main/scala/com/github/j5ik2o/dddbase/example/repository/skinny/UserAccountRepositoryBySkinny.scala)
- [for Memcached](example/src/main/scala/com/github/j5ik2o/dddbase/example/repository/memcached/UserAccountRepositoryOnMemcached.scala)
- [for Redis](example/src/main/scala/com/github/j5ik2o/dddbase/example/repository/redis/UserAccountRepositoryOnRedis.scala)
- [for DynamoDB](example/src/main/scala/com/github/j5ik2o/dddbase/example/repository/dynamodb/UserAccountRepositoryOnDynamoDB.scala)
- [for Memory(Guava Cache)](example/src/main/scala/com/github/j5ik2o/dddbase/example/repository/memory/UserAccountRepositoryOnMemory.scala)
- [for Free](example/src/main/scala/com/github/j5ik2o/dddbase/example/repository/free/UserAccountRepositoryByFree.scala)

### Usage

- for Slick3

```scala
val userAccountRepository: UserAccountRepository[BySlick] = UserAccountRepository.bySlick(dbConfig.profile, dbConfig.db)
val resultTask: Task[UserAccount] = for {
  _ <- userAccountRepository.store(userAccount)
  result <- userAccountRepository.resolveBy(userAccount.id)
} yield result

val resultFuture: Future[UserAccount] = resultTask.runToFuture
```

- for SkinnyORM

```scala
val userAccountRepository: UserAccountRepository[BySkinny] = UserAccountRepository.bySkinny
val resultTask: Task[UserAccount] = for {
  _ <- userAccountRepository.store(userAccount)
  result <- userAccountRepository.resolveBy(userAccount.id)
} yield result

val resultFuture: Future[UserAccount] = resultTask.run(AutoSession).runToFuture
```

- for Memcached

```scala
val userAccountRepository: UserAccountRepository[OnMemcached] = UserAccountRepository.onMemcached(expireDuration = 5 minutes)
val resultFuture: Future[UserAccount] = connectionPool
  .withConnectionF { con =>
    (for {
      _ <- userAccountRepository.store(userAccount)
      r <- userAccountRepository.resolveById(userAccount.id)
    } yield r).run(con)
  }
  .runToFuture
```

- for Redis

```scala
val userAccountRepository: UserAccountRepository[OnRedis] = UserAccountRepository.onRedis(expireDuration = 5 minutes)
val resultFuture: Future[UserAccount] = connectionPool
  .withConnectionF { con =>
    (for {
      _ <- userAccountRepository.store(userAccount)
      r <- userAccountRepository.resolveById(userAccount.id)
    } yield r).run(con)
  }
  .runToFuture
```

- for DynamoDB

```scala
val userAccountRepository: UserAccountRepository[OnDynamoDB] = UserAccountRepository.onDynamoDB(dynamoDbAsyncClient)
val resultFuture: Future[UserAccount] = (for {
  _ <- userAccountRepository.store(userAccount)
  r <- userAccountRepository.resolveById(userAccount.id)
} yield r).runToFuture
```

- for Memory(Guava Cache)

```scala
val userAccountRepository: UserAccountRepository[OnMemory] = UserAccountRepository.onMemory(expireAfterWrite = Some(5 minutes))
val resultFuture: Future[UserAccount] = (for {
  _ <- repository.store(userAccount)
  r <- repository.resolveById(userAccount.id)
} yield r).runToFuture
```

- for Free

```scala
val free: UserAccountRepository[ByFree] = UserAccountRepositoryByFree
val program: Free[UserRepositoryDSL, UserAccount] = for {
  _      <- free.store(userAccount)
  result <- free.resolveById(userAccount.id)
} yield result

val slick = UserAccountRepository.bySlick(dbConfig.profile, dbConfig.db)
val resultTask: Task[UserAccount] = UserAccountRepositoryOnFree.evaluate(slick)(program)
val resultFuture: Future[UserAccount] = evalResult.runToFuture

// if evaluation by skinny 
// val skinny     = UserAccountRepository.bySkinny
// val resultTask: Task[UserAccount] = UserAccountRepositoryOnFree.evaluate(skinny)(program)
// val resultFuture: Future[UserAccount] = evalResult.run(AutoSession).runToFuture
```
