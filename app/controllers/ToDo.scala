package controllers

import javax.inject._
import play.api.mvc._
import scala.concurrent._
import persistence.ToDoRepository
import model.{ ToDo, ToDoCategory }
import model.viewValue.ViewValueToDo

@Singleton
class ToDoController @Inject() (
  val controllerComponents: ControllerComponents,
  todoRepo:                 ToDoRepository
)(implicit ec:              ExecutionContext) extends BaseController {

  def index() = Action.async { implicit request: Request[AnyContent] =>
    for {
      todosWithCategories <- todoRepo.getTodosWithCategories()
    } yield {
      Ok(views.html.ToDo(
        ViewValueToDo(
          title  = "ToDo List",
          cssSrc = Seq("todo.css"),
          jsSrc  = Seq("todo.js"),
          todos  = todosWithCategories
        )
      ))
    }
  }

  // ToDoの削除メソッド
  def deleteToDo(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    todoRepo.remove(ToDo.Id(id)).map {
      case Some(_) => NoContent // 成功時にHTTP 204 (No Content) を返す
      case None    => NotFound  // 見つからなければ404を返す
    }
  }

}
