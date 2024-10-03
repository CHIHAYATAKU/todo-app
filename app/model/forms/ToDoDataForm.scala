package model.forms

import model.ToDoCategory
import play.api.data._
import play.api.data.Forms._

case class ToDoData(title: String, body: String, categoryId: ToDoCategory.Id)

object ToDoDataForm {
  // 正規表現を使って全角・半角の文字を許可する
  val titleRegex = "^[\\p{L}\\p{N}ー\\s\\p{P}]{1,255}$".r
  val bodyRegex  = "^[\\p{L}\\p{N}ー\\s\\p{P}]{1,1000}$".r

  val form: Form[ToDoData] = Form(
    mapping(
      "title"      -> nonEmptyText
        .verifying("無効な文字が含まれています（全角・半角可、改行不可）", title => titleRegex.pattern.matcher(title).matches)
        .verifying("タイトルは255文字以内で入力してください", _.length <= 255),
      "body"       -> text
        .verifying("無効な文字が含まれています（全角・半角可）", body => bodyRegex.pattern.matcher(body).matches)
        .verifying("本文は1000文字以内で入力してください", _.length <= 1000),
      "categoryId" -> longNumber.transform[ToDoCategory.Id](ToDoCategory.Id.apply, _.toLong)
    )(ToDoData.apply)(ToDoData.unapply)
  )
}
