# scala-ddd-base

[![CircleCI](https://circleci.com/gh/j5ik2o/scala-ddd-base.svg?style=svg)](https://circleci.com/gh/j5ik2o/scala-ddd-base)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/0565a420c74b4c4c8df3e1896b5b0e3e)](https://www.codacy.com/project/j5ik2o/scala-ddd-base/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=j5ik2o/scala-ddd-base&amp;utm_campaign=Badge_Grade_Dashboard)
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
  // "com.github.j5ik2o" %% "scala-ddd-base-memcached" % "1.0.6"
  // "com.github.j5ik2o" %% "scala-ddd-base-memory" % "1.0.6" 
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
  // "com.github.j5ik2o" %% "scala-ddd-base-memcached" % "1.0.6-SNAPSHOT"
  // "com.github.j5ik2o" %% "scala-ddd-base-memory" % "1.0.6-SNAPSHOT"
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

The following provides an implementation for each ORM/KVS.

- AggregateSingleReadFeature
- AggregateSingleWriteFeature
- AggregateMultiReadFeature
- AggregateMultiWriteFeature
- AggregateSingleDeleteFeature

The supported ORM/KVS is below.

- Slick(JDBC)
- SkinnyORM(JDBC)
- Redis([reactive-redis-core](https://github.com/j5ik2o/reactive-redis))
- Memcached([reactive-memcached-core](https://github.com/j5ik2o/reactive-memcached))

## Example

Please mix in the core and support traits to your implementation. 
Slick, SkinnyORM, Memcached, Redis, Memory etc. You can also choose the implementation as you like.

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

  type OnRedis[A]     = ReaderT[Task, RedisConnection, A]
  type OnMemcached[A] = ReaderT[Task, MemcachedConnection, A]
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

  def onMemory(minSize: Option[Int] = None,
               maxSize: Option[Int] = None,
               expireDuration: Option[Duration] = None,
               concurrencyLevel: Option[Int] = None,
               maxWeight: Option[Int] = None): UserAccountRepository[OnMemory] =
    new UserAccountRepositoryOnMemory(minSize, maxSize, expireDuration, concurrencyLevel, maxWeight)
    
}
```

- [for Slick3](blob/master/example/src/main/scala/com/github/j5ik2o/dddbase/example/repository/skinny/UserAccountRepositoryBySkinny.scala)
- [for SkinnyORM](blob/master/example/src/main/scala/com/github/j5ik2o/dddbase/example/repository/skinny/UserAccountRepositoryBySkinny.scala)
- [for Memcached](blob/master/example/src/main/scala/com/github/j5ik2o/dddbase/example/repository/memcached/UserAccountRepositoryOnMemcached.scala)
- [for Redis](blob/master/example/src/main/scala/com/github/j5ik2o/dddbase/example/repository/redis/UserAccountRepositoryOnRedis.scala)
- [for Memory(Guava Cache)](blob/master/example/src/main/scala/com/github/j5ik2o/dddbase/example/repository/memory/UserAccountRepositoryOnMemory.scala)
- [for Free](blob/master/example/src/main/scala/com/github/j5ik2o/dddbase/example/repository/free/UserAccountRepositoryByFree.scala)

### Usage

- for Slick3

```scala
val userAccountRepository: UserAccountRepository[BySlick] = UserAccountRepository.bySlickWithTask(dbConfig.profile, dbConfig.db)
val resultTask: Task[UserAccount] = for {
  _ <- userAccountRepository.store(userAccount)
  result <- userAccountRepository.resolveBy(userAccount.id)
} yield result

val resultFuture: Future[UserAccount] = resultTask.runAsync
```

- for SkinnyORM

```scala
val userAccountRepository: UserAccountRepository[BySkinny] = UserAccountRepository.bySkinnyWithTask
val resultTask: Task[UserAccount] = for {
  _ <- userAccountRepository.store(userAccount)
  result <- userAccountRepository.resolveBy(userAccount.id)
} yield result

val resultFuture: Future[UserAccount] = resultTask.run(AutoSession).runAsync
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
  .runAsync
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
  .runAsync
```

- for Memory(Guava Cache)

```scala
val repository = UserAccountRepository.onMemory(expireDuration = Some(5 minutes))
val resultFuture: Future[UserAccount] = (for {
  _ <- repository.store(userAccount)
  r <- repository.resolveById(userAccount.id)
} yield r).runAsync
```

- for Free

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
