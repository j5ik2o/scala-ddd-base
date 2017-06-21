package example

import com.github.j5ik2o.scala.ddd.functional.Aggregate

import scala.reflect.{ ClassTag, classTag }

case class User(id: Option[UserId], name: String) extends Aggregate {
  override type AggregateType = User
  override type IdType = UserId
  override protected val tag: ClassTag[User] = classTag[User]
}
