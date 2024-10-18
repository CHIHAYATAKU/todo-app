package model.viewValue

import model.ToDoCategory
import model.viewValue.common.ViewValueCommon

// Categoryページのviewvalue
case class ViewValueCategory(
  title:      String,
  cssSrc:     Seq[String],
  jsSrc:      Seq[String],
  categories: Seq[ToDoCategory#EmbeddedId]
) extends ViewValueCommon
