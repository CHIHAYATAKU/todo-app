package model

import ixias.model._
import ixias.util._
import java.time.LocalDateTime

// Categoryを表すモデル
//~~~~~~~~~~~~~~~~~~~~
case class ToDoCategory(
  id:        Option[ToDoCategory.Id],
  name:      String,
  slug:      String,
  color:     ToDoCategory.Color,
  updatedAt: LocalDateTime = NOW,
  createdAt: LocalDateTime = NOW
) extends EntityModel[ToDoCategory.Id]

// コンパニオンオブジェクト
//~~~~~~~~~~~~~~~~~~~~~~~~
object ToDoCategory {

  val Id = the[Identity[Id]]
  type Id         = Long @@ ToDoCategory
  type WithNoId   = Entity.WithNoId[Id, ToDoCategory]
  type EmbeddedId = Entity.EmbeddedId[Id, ToDoCategory]

  // ステータス定義
  //~~~~~~~~~~~~~~~~~
  sealed abstract class Color(val code: Short, val name: String) extends EnumStatus

  object Color extends EnumStatus.Of[Color] {
    case object FRONT extends Color(code = 1, name = "#F2A1A1")
    case object BACK  extends Color(code = 2, name = "#A1B3F2")
    case object INFRA extends Color(code = 3, name = "#F2E1A1")

  }
}
