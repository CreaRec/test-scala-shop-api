package dto.request

case class OrderDTO(id: Option[Int], idUser: Option[Int], amount: Option[Int], items: Option[Seq[Int]])
