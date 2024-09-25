package controllers

import play.api.data._
import play.api.data.Forms._
import javax.inject._
import play.api.mvc._
import scala.concurrent._
import persistence.{ ToDoRepository, ToDoCategoryRepository }
import model.{ ToDo, ToDoCategory }
import model.viewValue.ViewValueToDo

@Singleton
class ToDoController @Inject() (
  val controllerComponents: ControllerComponents,
  todoRepo:                 ToDoRepository,
  categoryRepo:             ToDoCategoryRepository
)(implicit ec:              ExecutionContext) extends BaseController {

  def index() = Action.async { implicit request: Request[AnyContent] =>
    for {
      todosWithCategories <- todoRepo.getTodosWithCategories()
      categories          <- categoryRepo.getAll()
    } yield {
      Ok(views.html.ToDo(
        ViewValueToDo(
          title      = "ToDo List",
          cssSrc     = Seq("todo.css"),
          jsSrc      = Seq("todo.js"),
          todos      = todosWithCategories,
          categories = categories
        )
      ))
    }
  }

  // ToDoの削除メソッド
  def deleteToDo(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    todoRepo.remove(ToDo.Id(id)).map {
      case Some(_) =>
        Redirect(routes.ToDoController.index()).flashing("success" -> "ToDoが削除されました！")
      case None    =>
        Redirect(routes.ToDoController.index()).flashing("error" -> "削除したいToDoが見つかりませんでした")
    }
  }
}
