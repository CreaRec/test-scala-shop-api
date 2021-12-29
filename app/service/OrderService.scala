package service

import dto.request.OrderDTO
import exception.{EntityNotFoundException, ParamenerRequiredException}
import model.{Item, Order, OrderItem, User}
import scalikejdbc._

class OrderService {
  val oc: scalikejdbc.ColumnName[Order] = Order.column
  val oic: scalikejdbc.ColumnName[OrderItem] = OrderItem.column
  val (o, oi, u, i) = (Order.syntax("o"), OrderItem.syntax("oi"), User.syntax("u"), Item.syntax("i"))

  def get(id: Int): Option[Order] = DB.readOnly { implicit session =>
    withSQL[Order] {
      select.from(Order as o)
        .leftJoin(OrderItem as oi).on(oi.idOrder, o.id)
        .leftJoin(Item as i).on(i.id, oi.idItem)
        .where.eq(o.id, id) }
      .one(Order(o))
      .toMany(Item.opt(i))
      .map{(order: Order, items: scala.collection.Seq[Item]) => order.copy(items = items)}
      .single()
      .apply();
  }

  def all: Seq[Order] = DB.readOnly { implicit session =>
    withSQL[Order] {
      select.from(Order as o)
        .leftJoin(OrderItem as oi).on(oi.idOrder, o.id)
        .leftJoin(Item as i).on(i.id, oi.idItem) }
      .one(Order(o))
      .toMany(Item.opt(i))
      .map{(order: Order, items: scala.collection.Seq[Item]) => order.copy(items = items)}
      .list
      .apply();
  }

  def getByUser(idUser: Int): Seq[Order] = DB.readOnly { implicit session =>
    withSQL[Order] {
      select.from(Order as o)
        .leftJoin(OrderItem as oi).on(oi.idOrder, o.id)
        .leftJoin(Item as i).on(i.id, oi.idItem)
        .where.eq(o.idUser, idUser) }
      .one(Order(o))
      .toMany(Item.opt(i))
      .map{(order: Order, items: scala.collection.Seq[Item]) => order.copy(items = items)}
      .list
      .apply();
  }

  def add(orderDTO: OrderDTO)(implicit session: DBSession = AutoSession): Long = {
    var resultId: Long = 0
    if (orderDTO.id.isEmpty) {
      if (orderDTO.idUser.isEmpty) {
        throw new ParamenerRequiredException("User id is required for creating new order")
      }
      resultId = withSQL {
        insert.into(Order)
          .columns(oc.idUser, oc.amount)
          .values(orderDTO.idUser, orderDTO.amount)
      }.updateAndReturnGeneratedKey().apply()
    } else {
      var order = get(orderDTO.id.get).orNull
      if (order != null) {
        order = Order.mergeToModel(order, orderDTO)
        resultId = withSQL {
          update(Order)
            .set(oc.amount -> order.amount)
            .where.eq(oc.id, order.id)
        }.updateAndReturnGeneratedKey.apply()
      } else {
        throw new EntityNotFoundException("Order with id " + orderDTO.id.get + " not found")
      }
    }
    if (resultId != 0 && orderDTO.items.nonEmpty) {
      withSQL {
        delete.from(OrderItem as oi).where.eq(oi.idOrder, resultId) }
        .update()
        .apply()
      if (orderDTO.items.get.nonEmpty) {
        for (item <- orderDTO.items.get) {
          withSQL {
            insert.into(OrderItem)
              .columns(oic.idOrder, oic.idItem)
              .values(resultId, item)
          }.update().apply()
        }
      }
    }
    resultId
  }

  def remove(idOrder: Int)(implicit session: DBSession = AutoSession): Unit = {
    withSQL {
      delete.from(Order as o).where.eq(o.id, idOrder) }
      .update()
      .apply();
  }
}