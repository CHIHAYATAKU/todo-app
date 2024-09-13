package controllers

import javax.inject._
import play.api.mvc._
import scala.concurrent._
import scala.concurrent.duration._
import model.{ ToDo, ToDoCategory, ViewValueToDo }
import play.api.i18n.{ I18nSupport, Messages }
import persistence.{ ToDoRepository, ToDoCategoryRepository }
import play.twirl.api.Html

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
          cssSrc = Seq("styles.css"),
          jsSrc  = Seq("scripts.js")
        ),
        todosWithCategories
      ))
    }
  }
}
