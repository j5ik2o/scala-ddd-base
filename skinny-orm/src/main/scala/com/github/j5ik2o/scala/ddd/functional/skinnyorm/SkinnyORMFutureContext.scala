package com.github.j5ik2o.scala.ddd.functional.skinnyorm

import scalikejdbc.DBSession

import scala.concurrent.ExecutionContext

case class SkinnyORMFutureContext(ec: ExecutionContext, dBSession: DBSession)
