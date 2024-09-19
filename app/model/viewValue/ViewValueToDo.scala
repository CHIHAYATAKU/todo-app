package model.viewValue

import model.{ ToDo, ToDoCategory }
import model.viewValue.common.ViewValueCommon
// ToDoページのviewvalue
case class ViewValueToDo(
  title:  String,
  cssSrc: Seq[String],
  jsSrc:  Seq[String],
  todos:  Seq[(ToDo#EmbeddedId, Option[ToDoCategory#EmbeddedId])],
) extends ViewValueCommon
