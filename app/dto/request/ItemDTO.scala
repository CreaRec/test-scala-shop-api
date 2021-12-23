package dto.request

case class ItemDTO(id: Option[Int], barcode: Option[String], name: Option[String], price: Option[Int], idCategory: Option[Int])
