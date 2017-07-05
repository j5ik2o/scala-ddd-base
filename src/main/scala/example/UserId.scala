package example

import com.github.j5ik2o.scala.ddd.functional.AggregateId

case class UserId(value: Long) extends AggregateId {
  override type IdType        = Long
  override type AggregateType = User
}
