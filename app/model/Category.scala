package model

import play.api.libs.json.{Format, Json}
import scalikejdbc._

case class Category(id: Int, name: String)

object Category extends SQLSyntaxSupport[Category] {
  override val tableName = "category"
  implicit val format: Format[Category] = Json.format

  def apply(g: SyntaxProvider[Category])(rs: WrappedResultSet): Category = apply(g.resultName)(rs)
  def apply(g: ResultName[Category])(rs: WrappedResultSet): Category = {
    val id = rs.int(g.id)
    val name = rs.string(g.name)
    Category(id, name)
  }
}



