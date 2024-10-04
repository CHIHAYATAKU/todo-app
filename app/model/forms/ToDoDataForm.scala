package model.forms

import model.ToDoCategory
import play.api.data._
import play.api.data.Forms._

case class ToDoData(title: String, body: String, categoryId: ToDoCategory.Id)

object ToDoDataForm {
  val titleRegex = "^[\\p{L}\\p{N}ー\\s\\p{P}]+$".r
  val bodyRegex  = "^[\\p{L}\\p{N}ー\\s\\p{P}]+$".r

  val form: Form[ToDoData] = Form(
    mapping(
      "title"      -> nonEmptyText(maxLength = 255)
        .verifying("無効な文字が含まれています（全角・半角可、改行不可）", title => titleRegex.pattern.matcher(title).matches),
      "body"       -> text(maxLength = 1000),
      "categoryId" -> longNumber.transform[ToDoCategory.Id](ToDoCategory.Id.apply, _.toLong)
    )(ToDoData.apply)(ToDoData.unapply)
  )
}
