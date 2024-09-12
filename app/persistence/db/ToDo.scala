package persistence.db

import java.time.LocalDateTime
import ixias.slick.jdbc.MySQLProfile.api._
import ixias.slick.builder._
import model.ToDo
import persistence.db.ToDoCategoryTable

import java.time

// UserTable: Userテーブルへのマッピングを行う
//~~~~~~~~~~~~~~
case class ToDoTable(tag: Tag) extends Table[ToDo](tag, "todo") {
  def id         = column[ToDo.Id]("id", UInt64, O.PrimaryKey, O.AutoInc)
  def categoryId = column[Long]("categoryId", UInt64)
  def title      = column[String]("title", Utf8Char255)
  def body       = column[String]("body", Text)
  def state      = column[ToDo.ToDoState]("state", UInt8)
  def updatedAt  = column[LocalDateTime]("updated_at", TsCurrent)
  def createdAt  = column[LocalDateTime]("created_at", Ts)

  // DB <=> Scala の相互のmapping定義
  def * = (id.?, categoryId, title, body, state, updatedAt, createdAt).<>(
    (ToDo.apply _).tupled,
    (ToDo.unapply _).andThen(_.map(_.copy(
      _6 = LocalDateTime.now()
    )))
  )
}
