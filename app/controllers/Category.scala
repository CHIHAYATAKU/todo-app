package controllers

import javax.inject._
import play.api.mvc._
import scala.concurrent._
import persistence.ToDoCategoryRepository
import model.viewValue.ViewValueCategory

@Singleton
class CategoryController @Inject() (
  val controllerComponents: ControllerComponents,
  categoryRepo:             ToDoCategoryRepository
)(implicit ec:              ExecutionContext) extends BaseController {

  def index() = Action.async { implicit request: Request[AnyContent] =>
    categoryRepo.getAll().map { categories =>
      Ok(views.html.Category(
        ViewValueCategory(
          title      = "Categories",
          cssSrc     = Seq("Category.css"),
          jsSrc      = Seq("Category.js"),
          categories = categories
        )
      ))
    }
  }
}
