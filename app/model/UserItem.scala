package model

import play.api.libs.json.{Format, Json}
import scalikejdbc._

case class UserItem(idUser: Int, idItem: Int, user: Option[User], item: Option[Item])

object UserItem extends SQLSyntaxSupport[UserItem] {
  override val tableName = "user_item"
  implicit val format: Format[UserItem] = Json.format

  def apply(g: SyntaxProvider[UserItem])(rs: WrappedResultSet): UserItem = apply(g.resultName)(rs)
  def apply(g: SyntaxProvider[UserItem], i: SyntaxProvider[Item])(
    rs: WrappedResultSet
  ): UserItem = {
    val userItem = apply(g)(rs)
    userItem.copy(item = Some(Item(i)(rs)))
  }
  def apply(g: SyntaxProvider[UserItem], u: SyntaxProvider[User], i: SyntaxProvider[Item])(
    rs: WrappedResultSet
  ): UserItem = {
    val userItem = apply(g)(rs)
    userItem.copy(user = Some(User(u)(rs)), item = Some(Item(i)(rs)))
  }

  def apply(g: ResultName[UserItem])(rs: WrappedResultSet): UserItem = {
    val idUser = rs.int(g.idUser)
    val idItem = rs.int(g.idItem)
    UserItem(idUser, idItem, None, None)
  }
}




