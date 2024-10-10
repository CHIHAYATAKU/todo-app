package model.viewValue

import play.api.data.Form
import model.{ ToDo, ToDoCategory }
import model.forms.ToDoData
import model.viewValue.common.ViewValueCommon

// ToDoUpdateページのviewvalue
case class ViewValueToDoUpdate(
  title:      String,
  cssSrc:     Seq[String],
  jsSrc:      Seq[String],
  toDoForm:   Form[ToDoData],
  todoId:     ToDo.Id,
  categories: Seq[ToDoCategory#EmbeddedId],
) extends ViewValueCommon
