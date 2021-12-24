package controllers

import dto.request.UserItemDTO
import play.api.libs.json._
import play.api.mvc._
import service.CartService

import javax.inject._

@Singleton
class CartController @Inject()(val controllerComponents: ControllerComponents,
                               val cartService: CartService) extends BaseController {

  def getByUserId(idUser: Int) = Action { implicit request =>
    val items = cartService.getByUser(idUser).map(item => Json.toJson(item))
    Ok(Json.toJson(items))
  }

  def add() = Action(parse.json) { implicit request =>
    val userItemDTO = request.body.validate[UserItemDTO](Json.format[UserItemDTO]).get
    cartService.add(userItemDTO)
    Ok
  }

  def delete(idUser: Int, idItem: Int) = Action { implicit request =>
    cartService.remove(idUser, idItem)
    Ok
  }
}
