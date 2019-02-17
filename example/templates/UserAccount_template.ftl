package com.github.j5ik2o.dddbase.example.dao

package slick {
import com.github.j5ik2o.dddbase.slick.SlickDaoSupport

<#assign softDelete=false>
trait ${className}Component extends SlickDaoSupport {
  import profile.api._

  case class ${className}Record(
<#list primaryKeys as primaryKey>
    ${primaryKey.propertyName}: ${primaryKey.propertyTypeName}<#if primaryKey_has_next>,</#if></#list><#if primaryKeys?has_content>,</#if>
<#list columns as column>
    <#if column.columnName == "status">
        <#assign softDelete=true>
    </#if>
    <#if column.nullable>    ${column.propertyName}: Option[${column.propertyTypeName}]<#if column_has_next>,</#if>
    <#else>    ${column.propertyName}: ${column.propertyTypeName}<#if column_has_next>,</#if>
    </#if>
</#list>
  ) extends <#if softDelete == false>Record<#else>SoftDeletableRecord</#if>

  case class ${className}s(tag: Tag) extends TableBase[${className}Record](tag, "${tableName}")<#if softDelete == true> with SoftDeletableTableSupport[${className}Record]</#if> {
<#list primaryKeys as primaryKey>
    def ${primaryKey.propertyName}: Rep[${primaryKey.propertyTypeName}] = column[${primaryKey.propertyTypeName}]("${primaryKey.columnName}")
</#list>
<#list columns as column>
    <#if column.nullable>
    def ${column.propertyName}: Rep[Option[${column.propertyTypeName}]] = column[Option[${column.propertyTypeName}]]("${column.columnName}")
    <#else>
    def ${column.propertyName}: Rep[${column.propertyTypeName}] = column[${column.propertyTypeName}]("${column.columnName}")
    </#if>
</#list>
    def pk  = primaryKey("pk", (<#list primaryKeys as primaryKey>${primaryKey.propertyName}<#if primaryKey_has_next>,</#if></#list>))
    override def * = (<#list primaryKeys as primaryKey>${primaryKey.propertyName}<#if primaryKey_has_next>,</#if></#list><#if primaryKeys?has_content>,</#if><#list columns as column>${column.propertyName}<#if column_has_next>,</#if></#list>) <> (${className}Record.tupled, ${className}Record.unapply)
  }

  object ${className}Dao extends TableQuery(${className}s)

}

}

package skinny {

import com.github.j5ik2o.dddbase.skinny.SkinnyDaoSupport
import scalikejdbc._
import _root_.skinny.orm._

trait ${className}Component extends SkinnyDaoSupport {

  case class ${className}Record(
<#list primaryKeys as primaryKey>
    ${primaryKey.propertyName}: ${primaryKey.propertyTypeName}<#if primaryKey_has_next>,</#if></#list><#if primaryKeys?has_content>,</#if>
<#list columns as column>
    <#if column.columnName == "status">
        <#assign softDelete=true>
    </#if>
    <#if column.nullable>    ${column.propertyName}: Option[${column.propertyTypeName}]<#if column_has_next>,</#if>
    <#else>    ${column.propertyName}: ${column.propertyTypeName}<#if column_has_next>,</#if>
    </#if>
</#list>
  ) extends Record

  object ${className}Dao extends Dao[${className}Record] {

      override def useAutoIncrementPrimaryKey: Boolean = false

      override val tableName: String = "${tableName}"

      override protected def toNamedValues(record: ${className}Record): Seq[(Symbol, Any)] = Seq(
<#list columns as column>       '${column.name} -> record.${column.propertyName}<#if column.name?ends_with("id") || column.name?ends_with("Id")>.value</#if><#if column_has_next>,</#if>
</#list>
    )

      override def defaultAlias: Alias[UserAccountRecord] = createAlias("${className[0]?lower_case}")

      override def extract(rs: WrappedResultSet, s: ResultName[${className}Record]): ${className}Record = autoConstruct(rs, s)


  }


}

}