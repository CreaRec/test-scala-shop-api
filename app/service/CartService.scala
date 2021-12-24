package service

import dto.request.UserItemDTO
import model.{Item, User, UserItem}
import scalikejdbc._

class CartService {
  val uic: scalikejdbc.ColumnName[UserItem] = UserItem.column
  val (ui, u, i) = (UserItem.syntax("ui"), User.syntax("u"), Item.syntax("i"))

  def getByUser(idUser: Int): Seq[UserItem] = DB.readOnly { implicit session =>
    withSQL {
      select.from(UserItem as ui).leftJoin(Item as i).on(ui.idItem, i.id).where.eq(ui.idUser, idUser) }
      .map(UserItem(ui, i))
      .list
      .apply();
  }

  def add(userItemDTO: UserItemDTO)(implicit session: DBSession = AutoSession): Unit = {
    withSQL {
      insert.into(UserItem)
        .columns(uic.idUser, uic.idItem)
        .values(userItemDTO.idUser, userItemDTO.idItem)
    }.update().apply()
  }

  def remove(idUser: Int, idItem: Int)(implicit session: DBSession = AutoSession): Unit = {
    withSQL {
      delete.from(UserItem as ui).where.eq(ui.idUser, idUser).and.eq(ui.idItem, idItem) }
      .update()
      .apply();
  }
}