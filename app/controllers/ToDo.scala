package controllers

import javax.inject._
import play.api.mvc._
import scala.concurrent._
import models.forms.ToDoDataForm
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

  def showCreateToDoForm() = Action.async { implicit request: Request[AnyContent] =>
    categoryRepo.getAll().map { categories =>
      Ok(views.html.ToDoCreateForm(ToDoDataForm.form, categories)(request, messagesApi.preferred(request)))
    }
  }

def createToDo() = Action.async { implicit request: Request[AnyContent] =>
  // カテゴリを取得
  categoryRepo.getAll().flatMap { categories =>
    ToDoDataForm.form.bindFromRequest().fold(
      formWithErrors => {
        // BadRequest時にカテゴリをビューに渡す
        Future.successful(BadRequest(views.html.ToDoCreateForm(formWithErrors, categories)(request, messagesApi.preferred(request))))
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
