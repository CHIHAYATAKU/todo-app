package model.viewValue

import play.api.data.Form
import model.{ ToDo, ToDoCategory }
import model.forms.ToDoData
import model.viewValue.common.ViewValueCommon
// ToDoCreateページのviewvalue
case class ViewValueToDoCreate(
  title:      String,
  cssSrc:     Seq[String],
  jsSrc:      Seq[String],
  toDoForm:   Form[ToDoData],
  categories: Seq[ToDoCategory#EmbeddedId]
) extends ViewValueCommon
