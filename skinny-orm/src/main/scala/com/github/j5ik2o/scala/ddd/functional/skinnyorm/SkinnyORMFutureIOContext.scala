package com.github.j5ik2o.scala.ddd.functional.skinnyorm

import scalikejdbc.DBSession

import scala.concurrent.ExecutionContext

case class SkinnyORMFutureIOContext(ec: ExecutionContext, dBSession: DBSession)
