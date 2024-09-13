package persistence.db

import java.time.LocalDateTime
import ixias.slick.jdbc.MySQLProfile.api._
import ixias.slick.builder._
import model.ToDoCategory

import java.time

// UserTable: Userテーブルへのマッピングを行う
//~~~~~~~~~~~~~~
case class ToDoCategoryTable(tag: Tag) extends Table[ToDoCategory](tag, "to_do_category") {
  def id        = column[ToDoCategory.Id]("id", UInt64, O.PrimaryKey, O.AutoInc)
  def name      = column[String]("name", Utf8Char255)
  def slug      = column[String]("slug", AsciiChar64)
  def color     = column[ToDoCategory.Color]("color", UInt8)
  def updatedAt = column[LocalDateTime]("updated_at", TsCurrent)
  def createdAt = column[LocalDateTime]("created_at", Ts)

  // DB <=> Scala の相互のmapping定義
  def * = (id.?, name, slug, color, updatedAt, createdAt).<>(
    (ToDoCategory.apply _).tupled,
    (ToDoCategory.unapply _).andThen(_.map(_.copy(
      _5 = LocalDateTime.now()
    )))
  )
}
