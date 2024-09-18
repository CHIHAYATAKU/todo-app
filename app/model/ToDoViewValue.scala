package model
// ToDoページのviewvalue
case class ViewValueToDo(
  title:  String,
  cssSrc: Seq[String],
  jsSrc:  Seq[String],
  todos:  Seq[(ToDo#EmbeddedId, Option[ToDoCategory#EmbeddedId])],
) extends ViewValueCommon
