package persistence

import com.zaxxer.hikari.HikariDataSource

import scala.concurrent.{ ExecutionContext, Future }
import ixias.slick.SlickRepository
import ixias.slick.jdbc.MySQLProfile.api._
import model.ToDoCategory
import persistence.db.ToDoCategoryTable
import javax.inject._

// ToDoCategoryRepository: ToDoCategoryTableへのクエリ発行を行うRepository層の定義
//~~~~~~~~~~~~~~~~~~~~~~
@Singleton
class ToDoCategoryRepository @Inject() (
  @Named("master") master: Database,
  @Named("slave") slave:   Database
)(implicit val ec:         ExecutionContext) extends SlickRepository[ToDoCategory.Id, ToDoCategory] {

  val todoCategoryTable = TableQuery[ToDoCategoryTable]

  /**
    * Get ToDoCategory Data
    */
  def getById(id: ToDoCategory.Id): Future[Option[ToDoCategory]] = {
    slave.run(todoCategoryTable.filter(_.id === id).result.headOption)
  }

  /**
    * Add ToDoCategory Data
    */
  def add(category: ToDoCategory#WithNoId): Future[ToDoCategory.Id] = {
    master.run(todoCategoryTable returning todoCategoryTable.map(_.id) += category.v)
  }

  /**
    * Update ToDoCategory Data
    */
  def update(entity: ToDoCategory#EmbeddedId): Future[Option[ToDoCategory#EmbeddedId]] = {
    master.run {
      todoCategoryTable.filter(_.id === entity.id).update(entity.v).map(_ > 0).map {
        case true  => Some(entity)
        case false => None
      }
    }
  }

  /**
    * Delete ToDoCategory Data
    */
  def remove(id: ToDoCategory.Id): Future[Option[ToDoCategory#EmbeddedId]] = {
    master.run {
      todoCategoryTable.filter(_.id === id).delete.map {
        case 0 => None
        case _ => Some(id.asInstanceOf[ToDoCategory#EmbeddedId])
      }
    }
  }
}
