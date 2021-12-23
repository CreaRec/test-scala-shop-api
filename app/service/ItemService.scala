package service

import dto.request.ItemDTO
import exception.EntityNotFoundException
import model.Item
import model.Category
import scalikejdbc._

class ItemService {
  val ic: scalikejdbc.ColumnName[Item] = Item.column
  val (i, c) = (Item.syntax("i"), Category.syntax("c"))

  def all: Seq[Item] = DB.readOnly { implicit session =>
    withSQL {
      select.from(Item as i).leftJoin(Category as c).on(i.idCategory, c.id) }
      .map(Item(i, c))
      .list
      .apply();
  }

  def get(id: Int): Option[Item] = DB.readOnly { implicit session =>
    withSQL {
      select.from(Item as i).leftJoin(Category as c).on(i.idCategory, c.id).where.eq(i.id, id) }
      .map(Item(i, c))
      .single
      .apply();
  }

  def getByBarcode(barcode: String): Option[Item] = DB.readOnly { implicit session =>
    withSQL {
      select.from(Item as i).leftJoin(Category as c).on(i.idCategory, c.id).where.eq(i.barcode, barcode) }
      .map(Item(i, c))
      .single
      .apply();
  }

  def add(itemDTO: ItemDTO)(implicit session: DBSession = AutoSession): Long = {
    if (itemDTO.id.isEmpty) {
      withSQL {
        insert.into(Item)
          .columns(ic.barcode, ic.name, ic.price, ic.idCategory)
          .values(itemDTO.barcode, itemDTO.name, itemDTO.price, itemDTO.idCategory)
      }.updateAndReturnGeneratedKey().apply()
    } else {
      var item = get(itemDTO.id.get).orNull
      if (item != null) {
        item = Item.mergeToModel(item, itemDTO)
        withSQL {
          update(Item)
            .set(ic.barcode -> itemDTO.barcode, ic.name -> itemDTO.name, ic.price -> itemDTO.price, ic.idCategory -> itemDTO.idCategory)
            .where.eq(ic.id, item.id)
        }.updateAndReturnGeneratedKey.apply()
      } else {
        throw new EntityNotFoundException("Item with id " + itemDTO.id.get + " not found")
      }
    }
  }

  def remove(id: Int)(implicit session: DBSession = AutoSession): Unit = {
    withSQL {
      delete.from(Item as i).where.eq(i.id, id) }
      .map(Item(i, c))
      .update()
      .apply();
  }
}