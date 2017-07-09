package com.github.j5ik2o.scala.ddd.functional.slick

import cats.Eq
import com.github.j5ik2o.scala.ddd.functional.Aggregate

import scala.reflect.{ classTag, ClassTag }

case class User(id: UserId, name: String) extends Aggregate {
  override type AggregateType = User
  override type IdType        = UserId
  override protected val tag: ClassTag[User] = classTag[User]
}

object User {
  implicit val userEq = new Eq[User] {
    override def eqv(x: User, y: User): Boolean = x == y
  }
}
