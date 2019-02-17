package com.github.j5ik2o.dddbase.example
import cats.data.ReaderT
import cats.free.Free
import com.github.j5ik2o.dddbase.example.repository.free.UserRepositoryDSL
import com.github.j5ik2o.reactive.memcached.MemcachedConnection
import com.github.j5ik2o.reactive.redis.RedisConnection
import monix.eval.Task
import scalikejdbc.DBSession

package object repository {
  type OnDynamoDB[A]  = Task[A]
  type OnRedis[A]     = ReaderT[Task, RedisConnection, A]
  type OnMemcached[A] = ReaderT[Task, MemcachedConnection, A]
  type OnMemory[A]    = Task[A]
  type BySlick[A]     = Task[A]
  type BySkinny[A]    = ReaderT[Task, DBSession, A]
  type ByFree[A]      = Free[UserRepositoryDSL, A]
}
