package persistence

import scala.concurrent.{ ExecutionContext, Future }
import ixias.slick.SlickRepository
import ixias.slick.jdbc.MySQLProfile.api._
import model.{ ToDo, ToDoCategory }
import persistence.db.{ ToDoTable, ToDoCategoryTable }
import javax.inject._

// ToDoRepository: ToDoTableへのクエリ発行を行うRepository層の定義
//~~~~~~~~~~~~~~~~~~~~~~
@Singleton
class ToDoRepository @Inject() (
  @Named("master") master: Database,
  @Named("slave") slave:   Database
)(implicit val ec:         ExecutionContext) extends SlickRepository[ToDo.Id, ToDo] {

  val todoTable         = TableQuery[ToDoTable]
  val todoCategoryTable = TableQuery[ToDoCategoryTable]

  /**
    * Get ToDo Data
    */
  def getById(id: ToDo.Id): Future[Option[ToDo]] = {
    slave.run(todoTable.filter(_.id === id).result.headOption)
  }

  /**
    * Get ToDo Data　With Category
    */
  def getTodosWithCategories(): Future[Seq[(ToDo, Option[ToDoCategory])]] = {
    val queryWithLeftJoin = for {
      (todo, categoryOpt) <- todoTable joinLeft todoCategoryTable on (_.categoryId === _.id)
    } yield (todo, categoryOpt)

    slave.run(queryWithLeftJoin.result)
  }

  /**
    * Add ToDo Data
    */
  def add(todo: ToDo#WithNoId): Future[ToDo.Id] = {
    master.run(todoTable returning todoTable.map(_.id) += todo.v)
  }

  /**
    * Update ToDo Data
    */
  def update(entity: ToDo#EmbeddedId): Future[Option[ToDo#EmbeddedId]] = {
    master.run {
      todoTable.filter(_.id === entity.id).update(entity.v).map(_ > 0).map {
        case true  => Some(entity)
        case false => None
      }
    }
  }

  /**
    * Delete ToDo Data
    */
  def remove(id: ToDo.Id): Future[Option[ToDo#EmbeddedId]] = {
    master.run {
      todoTable.filter(_.id === id).delete.map {
        case 0 => None
        case _ => Some(id.asInstanceOf[ToDo#EmbeddedId])
      }
    }
  }
}
