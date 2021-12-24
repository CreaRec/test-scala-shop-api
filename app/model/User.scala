package model

import play.api.libs.json.{Format, Json}
import scalikejdbc._

case class User(id: Option[Int], login: String, password: String)

object User extends SQLSyntaxSupport[User] {
  override val tableName = "users"
  implicit val format: Format[User] = Json.format

  def apply(g: SyntaxProvider[User])(rs: WrappedResultSet): User = apply(g.resultName)(rs)
  def apply(g: ResultName[User])(rs: WrappedResultSet): User = {
    val id = rs.intOpt(g.id)
    val login = rs.string(g.login)
    val password = rs.string(g.password)
    User(id, login, password)
  }
}

