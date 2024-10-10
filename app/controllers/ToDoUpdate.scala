package controllers

import java.time.LocalDateTime
import javax.inject._
import play.api.mvc._
import scala.concurrent._
import model.forms.{ ToDoData, ToDoDataForm }
import persistence.{ ToDoRepository, ToDoCategoryRepository }
import model.{ ToDo, ToDoCategory }
import model.viewValue.ViewValueToDoUpdate

@Singleton
class ToDoUpdateController @Inject() (
  val controllerComponents: ControllerComponents,
  todoRepo:                 ToDoRepository,
  categoryRepo:             ToDoCategoryRepository
)(implicit ec:              ExecutionContext) extends BaseController {

  def index(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    for {
      todoOpt    <- todoRepo.getById(ToDo.Id(id))
      categories <- categoryRepo.getAll()
    } yield {
      todoOpt match {
        case Some(todo) =>
          val viewValue = ViewValueToDoUpdate(
            title      = "Update ToDo",
            cssSrc     = Seq("todoUpdate.css"),
            jsSrc      = Seq("todoUpdate.js"),
            toDoForm   = ToDoDataForm.form.fill(ToDoData(todo.v.title, todo.v.body, todo.v.categoryId, Some(todo.v.state))),
            todoId     = ToDo.Id(id),
            categories = categories
          )

          Ok(views.html.ToDoUpdate(viewValue)(request, messagesApi.preferred(request)))
        case None       =>
          Redirect(routes.ToDoController.index()).flashing("error" -> "ToDoが見つかりませんでした")
      }
    }
  }

  def updateToDo(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    ToDoDataForm.form.bindFromRequest().fold(
      formWithErrors => {
        categoryRepo.getAll().map { categories =>
          BadRequest(views.html.ToDoUpdate(
            ViewValueToDoUpdate(
              title      = "ToDoを更新",
              cssSrc     = Seq("todoUpdate.css"),
              jsSrc      = Seq("todoUpdate.js"),
              toDoForm   = formWithErrors,
              todoId     = ToDo.Id(id),
              categories = categories
            )
          )(request, messagesApi.preferred(request)))
        }
      },
      toDoData => {
        toDoData.state match {
          case Some(state) =>
            val updatedTodo = ToDo(
              id         = Some(ToDo.Id(id)),
              categoryId = ToDoCategory.Id(toDoData.categoryId),
              title      = toDoData.title,
              body       = toDoData.body,
              state      = toDoData.state.get
            ).toEmbeddedId

            todoRepo.update(updatedTodo).map {
              case Some(_) =>
                Redirect(routes.ToDoController.index()).flashing("success" -> "ToDoが更新されました！")
              case None    =>
                Redirect(routes.ToDoController.index()).flashing("error" -> "更新に失敗しました")
            }
          case None        =>
            Future.successful(BadRequest("State is required"))
        }
      }
    )
  }
}
