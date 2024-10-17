package model.forms

import model.ToDoCategory
import model.ToDo.ToDoState
import play.api.data._
import play.api.data.Forms._

case class ToDoData(title: String, body: String, categoryId: ToDoCategory.Id, state: Option[ToDoState])

object ToDoDataForm {
  val titleRegex = "^[\\p{L}\\p{N}ー\\s\\p{P}]+$".r
  val bodyRegex  = "^[\\p{L}\\p{N}ー\\s\\p{P}]+$".r

  val form: Form[ToDoData] = Form(
    mapping(
      "title"      -> nonEmptyText(maxLength = 255)
        .verifying("無効な文字が含まれています（全角・半角可、改行不可）", title => titleRegex.pattern.matcher(title).matches),
      "body"       -> text(maxLength = 1000),
      "categoryId" -> longNumber.transform[ToDoCategory.Id](ToDoCategory.Id.apply, _.toLong),
      "state"      -> optional(number.transform[ToDoState](
        stateCode => {
          ToDoState.values.find(_.code == stateCode.toShort).getOrElse(
            throw new IllegalArgumentException("無効な状態コードです")
          )
        },
        _.code.toInt
      ))
    )(ToDoData.apply _)(ToDoData.unapply _)
  )
}
