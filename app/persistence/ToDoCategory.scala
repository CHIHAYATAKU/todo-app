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
    * Get all ToDoCategory Data
    */
  def getAll(): Future[Seq[ToDoCategory#EmbeddedId]] = {
    slave.run(todoCategoryTable.result).map { results =>
      results.map(_.toEmbeddedId)
    }
  }
}
