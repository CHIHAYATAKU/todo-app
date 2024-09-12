package model

import ixias.model._
import ixias.util._
import ixias.util.EnumStatus
import java.time.LocalDateTime

// ToDoを表すモデル
//~~~~~~~~~~~~~~~~~~~~
case class ToDo(
  id:         Option[ToDo.Id],
  categoryId: Long,
  title:      String,
  body:       String,
  state:      ToDo.ToDoState,
  updatedAt:  LocalDateTime = NOW,
  createdAt:  LocalDateTime = NOW
) extends EntityModel[ToDo.Id]

// コンパニオンオブジェクト
//~~~~~~~~~~~~~~~~~~~~~~~~
object ToDo {

  val Id = the[Identity[Id]]
  type Id         = Long @@ ToDo
  type WithNoId   = Entity.WithNoId[Id, ToDo]
  type EmbeddedId = Entity.EmbeddedId[Id, ToDo]

  // ステータス定義
  //~~~~~~~~~~~~~~~~~
  sealed abstract class ToDoState(val code: Short, val name: String) extends EnumStatus

  object ToDoState extends EnumStatus.Of[ToDoState] {
    case object TODO        extends ToDoState(code = 0, name = "TODO(着手前)")
    case object IN_PROGRESS extends ToDoState(code = 1, name = "進行中")
    case object DONE        extends ToDoState(code = 2, name = "完了")
  }
}
