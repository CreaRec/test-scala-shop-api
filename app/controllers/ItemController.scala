package controllers

import dto.request.ItemDTO
import play.api.libs.json._
import play.api.mvc._
import service.ItemService
import model.Item

import javax.inject._

@Singleton
class ItemController @Inject()(val controllerComponents: ControllerComponents,
                               val itemService: ItemService) extends BaseController {

  def get(barcode: Option[String]) = Action { implicit request =>
    if (barcode.nonEmpty) {
      val items = itemService.getByBarcode(barcode.get).orNull
      Ok(Json.toJson(items))
    } else {
      val items = itemService.all.map(item => Json.toJson(item))
      Ok(Json.toJson(items))
    }
  }

  def getById(id: Int) = Action { implicit request =>
    val items = itemService.get(id).orNull
    Ok(Json.toJson(items))
  }

  def add() = Action(parse.json) { implicit request =>
    val itemDTO = request.body.validate[ItemDTO](Json.format[ItemDTO]).get
    Ok(Json.toJson(itemService.add(itemDTO)))
  }

  def delete(id: Int) = Action { implicit request =>
    itemService.remove(id)
    Ok
  }
}
