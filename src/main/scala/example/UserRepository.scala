package example

import com.github.j5ik2o.scala.ddd.functional.Repository
import example.db.UserDaoComponent
import slick.jdbc.JdbcProfile

class UserRepository(val profile: JdbcProfile, val db: JdbcProfile#Backend#Database)
    extends Repository
    with UserDaoComponent {

  override type IdValueType   = Long
  override type IdType        = UserId
  override type AggregateType = User
  override type RecordType    = UserRecord
  override type TableType     = UserDef
  override val dao = UserDao

  protected def convertToRecord(aggregate: User): UserRecord = UserRecord(aggregate.id.value, aggregate.name)

  protected def convertToAggregate(record: UserRecord): User = User(UserId(record.id), record.name)

}
