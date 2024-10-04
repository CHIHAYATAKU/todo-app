package controllers

import javax.inject._
import play.api.mvc._
import scala.concurrent._
import model.forms.ToDoDataForm
import persistence.{ ToDoRepository, ToDoCategoryRepository }
import model.{ ToDo, ToDoCategory }
import model.viewValue.ViewValueToDoCreate

@Singleton
class ToDoCreateController @Inject() (
  val controllerComponents: ControllerComponents,
  todoRepo:                 ToDoRepository,
  categoryRepo:             ToDoCategoryRepository
)(implicit ec:              ExecutionContext) extends BaseController {

  def index() = Action.async { implicit request: Request[AnyContent] =>
    categoryRepo.getAll().map { categories =>
      Ok(views.html.ToDoCreate(
        ViewValueToDoCreate(
          title      = "Create ToDo",
          cssSrc     = Seq("todoCreate.css"),
          jsSrc      = Seq("todoCreate.js"),
          toDoForm   = ToDoDataForm.form,
          categories = categories
        )
      )(request, messagesApi.preferred(request)))
    }
  }

  // ToDoの追加メソッド
  def createToDo() = Action.async { implicit request: Request[AnyContent] =>
    ToDoDataForm.form.bindFromRequest().fold(
      formWithErrors => {
        // BadRequest時にカテゴリをビューに渡す
        categoryRepo.getAll().flatMap { categories =>
          Future.successful(BadRequest(views.html.ToDoCreate(
            ViewValueToDoCreate(
              title      = "Create ToDo",
              cssSrc     = Seq("todoCreate.css"),
              jsSrc      = Seq("todoCreate.js"),
              toDoForm   = formWithErrors, // エラーがあるフォームを渡す
              categories = categories
            )
          )(request, messagesApi.preferred(request))))
        }
      },
      toDoData => {
        val newTodo = ToDo(
          id         = None,
          categoryId = ToDoCategory.Id(toDoData.categoryId),
          title      = toDoData.title,
          body       = toDoData.body,
          state      = ToDo.ToDoState.TODO
        ).toWithNoId
        todoRepo.add(newTodo).map { _ =>
          Redirect(routes.ToDoController.index()).flashing("success" -> "ToDoが追加されました！")
        }
      }
    )
  }
}
