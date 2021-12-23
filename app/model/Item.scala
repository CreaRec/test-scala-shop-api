package model

import dto.request.ItemDTO
import play.api.libs.json.{Format, Json}
import scalikejdbc._

case class Item(id: Int, barcode: String, name: String, price: Int, idCategory: Int, category: Option[Category])

object Item extends SQLSyntaxSupport[Item] {
  override val tableName = "item"
  implicit val format: Format[Item] = Json.format

  def apply(g: SyntaxProvider[Item])(rs: WrappedResultSet): Item = apply(g.resultName)(rs)
  def apply(g: SyntaxProvider[Item], c: SyntaxProvider[Category])(
    rs: WrappedResultSet
  ): Item = {
    val item = apply(g)(rs)
    item.copy(category = Some(Category(c)(rs)))
  }
  def apply(g: ResultName[Item])(rs: WrappedResultSet): Item = {
    val id = rs.int(g.id)
    val barcode = rs.string(g.barcode)
    val name = rs.string(g.name)
    val price = rs.int(g.price)
    val idCategory = rs.int(g.idCategory)
    new Item(id, barcode, name, price, idCategory, None)
  }

  def mergeToModel(item: Item, itemDTO: ItemDTO): Item = {
    var barcode = item.barcode
    var name = item.name
    var price = item.price
    var idCategory = item.idCategory
    var category = item.category
    if (itemDTO.barcode.nonEmpty) {
      barcode = itemDTO.barcode.get
    }
    if (itemDTO.name.nonEmpty) {
      name = itemDTO.name.get
    }
    if (itemDTO.price.nonEmpty) {
      price = itemDTO.price.get
    }
    if (itemDTO.idCategory.nonEmpty) {
      idCategory = itemDTO.idCategory.get
      category = None
    }
    new Item(item.id, barcode, name, price, idCategory, category)
  }
}
