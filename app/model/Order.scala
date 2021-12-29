package model

import dto.request.OrderDTO
import play.api.libs.json.{Format, Json}
import scalikejdbc._

import java.time.LocalDateTime

case class Order(id: Int, idUser: Int, date: LocalDateTime, amount: Int, user: Option[User], items: scala.collection.Seq[Item])

object Order extends SQLSyntaxSupport[Order] {
  override val tableName = "orders"
  implicit val format: Format[Order] = Json.format

  def apply(g: SyntaxProvider[Order])(rs: WrappedResultSet): Order = apply(g.resultName)(rs)
  def apply(order: Order, items: scala.collection.Seq[Item]): Order = {
    order.copy(items = items)
  }
  def apply(g: ResultName[Order])(rs: WrappedResultSet): Order = {
    val id = rs.int(g.id)
    val idUser = rs.int(g.idUser)
    val date = rs.localDateTime(g.date)
    val amount = rs.int(g.amount)
    new Order(id, idUser, date, amount, None, Nil)
  }

  def mergeToModel(order: Order, orderDTO: OrderDTO): Order = {
    var amount = order.amount
    if (orderDTO.amount.nonEmpty) {
      amount = orderDTO.amount.get
    }
    order.copy(amount = amount)
  }
}



