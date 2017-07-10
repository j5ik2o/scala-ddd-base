package com.github.j5ik2o.scala.ddd.functional.cats

import scala.concurrent.Future

trait FutureStorageDriver extends StorageDriver {
  override type DSL[A]              = Future[A]
  override type SingleResultType[A] = Option[A]
}
