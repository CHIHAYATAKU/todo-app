package controllers

import javax.inject._
import play.api.mvc._
import scala.concurrent._
import persistence.ToDoRepository
import model.{ ToDo, ToDoCategory, ViewValueToDo }

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
        ),
      ))
    }
  }
}
