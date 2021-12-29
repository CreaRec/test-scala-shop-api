package model

import play.api.libs.json.{Format, Json}
import scalikejdbc._

case class OrderItem(idOrder: Int, idItem: Int, order: Option[Order], item: Option[Item])

object OrderItem extends SQLSyntaxSupport[OrderItem] {
  override val tableName = "order_item"
  implicit val format: Format[OrderItem] = Json.format

  def apply(g: SyntaxProvider[OrderItem])(rs: WrappedResultSet): OrderItem = apply(g.resultName)(rs)
  def apply(g: SyntaxProvider[OrderItem], i: SyntaxProvider[Item])(
    rs: WrappedResultSet
  ): OrderItem = {
    val orderItem = apply(g)(rs)
    orderItem.copy(item = Some(Item(i)(rs)))
  }
  def apply(g: SyntaxProvider[OrderItem], u: SyntaxProvider[Order], i: SyntaxProvider[Item])(
    rs: WrappedResultSet
  ): OrderItem = {
    val orderItem = apply(g)(rs)
    orderItem.copy(order = Some(Order(u)(rs)), item = Some(Item(i)(rs)))
  }

  def apply(g: ResultName[OrderItem])(rs: WrappedResultSet): OrderItem = {
    val idOrder = rs.int(g.idOrder)
    val idItem = rs.int(g.idItem)
    OrderItem(idOrder, idItem, None, None)
  }
}






