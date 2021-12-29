package controllers

import dto.request.OrderDTO
import play.api.libs.json._
import play.api.mvc._
import service.OrderService

import javax.inject._

@Singleton
class OrderController @Inject()(val controllerComponents: ControllerComponents,
                                val orderService: OrderService) extends BaseController {

  def getById(id: Int) = Action { implicit request =>
    Ok(Json.toJson(orderService.get(id)))
  }

  def get(idUser: Option[Int]) = Action { implicit request =>
    if (idUser.nonEmpty) {
      val items = orderService.getByUser(idUser.get).map(item => Json.toJson(item))
      Ok(Json.toJson(items))
    } else {
      val items = orderService.all.map(item => Json.toJson(item))
      Ok(Json.toJson(items))
    }
  }

  def getByUserId(idUser: Int) = Action { implicit request =>
    val items = orderService.getByUser(idUser).map(item => Json.toJson(item))
    Ok(Json.toJson(items))
  }

  def add() = Action(parse.json) { implicit request =>
    val orderDTO = request.body.validate[OrderDTO](Json.format[OrderDTO]).get
    orderService.add(orderDTO)
    Ok
  }

  def delete(idOrder: Int) = Action { implicit request =>
    orderService.remove(idOrder)
    Ok
  }
}
