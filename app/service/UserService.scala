package service

import dto.request.UserRegisterDTO
import model.User
import scalikejdbc._

class UserService {
  val uc: scalikejdbc.ColumnName[User] = User.column
  val u = User.syntax("u")

  def all: Seq[User] = DB.readOnly { implicit session =>
    withSQL {
      select.from(User as u) }.map(User(u)).list.apply();
  }

  def getByLogin(login: String): Option[User] = DB.readOnly { implicit session =>
    withSQL {
      select.from(User as u).where.eq(u.login, login) }.map(User(u)).first.apply();
  }

  def add(registerDTO: UserRegisterDTO)(implicit session: DBSession = AutoSession): Unit =
    withSQL {
      insert.into(User)
      .columns(uc.login, uc.password)
      .values(registerDTO.login, registerDTO.password)
    }.update.apply()
}