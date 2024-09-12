package persistence

import com.zaxxer.hikari.HikariDataSource

import scala.concurrent.{ ExecutionContext, Future }
import ixias.model._
import ixias.slick.SlickRepository
import ixias.slick.builder.{ DatabaseBuilder, HikariConfigBuilder }
import ixias.slick.jdbc.MySQLProfile.api._
import ixias.slick.model.DataSourceName
import model.{ ToDo, ToDoCategory }
import model.ToDo.Id
import persistence.db.{ ToDoTable, ToDoCategoryTable }
import slick.dbio.Effect
import slick.sql.FixedSqlAction

// ToDoRepository: ToDoTableへのクエリ発行を行うRepository層の定義
//~~~~~~~~~~~~~~~~~~~~~~
class ToDoRepository()(implicit val ec: ExecutionContext) extends SlickRepository[ToDo.Id, ToDo] {
  val master: Database = DatabaseBuilder.fromHikariDataSource(
    new HikariDataSource(HikariConfigBuilder.default(DataSourceName("ixias.db.mysql://master/user")).build())
  )
  val slave:  Database = DatabaseBuilder.fromHikariDataSource(
    new HikariDataSource(HikariConfigBuilder.default(DataSourceName("ixias.db.mysql://slave/user")).build())
  )

  val todoTable     = TableQuery[ToDoTable]
  val categoryTable = TableQuery[ToDoCategoryTable]

  /**
    * Get ToDo Data
    */
  def getById(id: ToDo.Id): Future[Option[ToDo]] = {
    slave.run(todoTable.filter(_.id === id).result.headOption)
  }

  /**
    * Get All ToDo Data with Category Information
    */
  def getAllWithCategories: Future[Seq[(ToDo, Option[ToDoCategory])]] = {
    val query = todoTable
      .joinLeft(categoryTable)
      .on(_.categoryId === _.id)
      .result
      .map(_.map {
        case (todo, categoryOpt) => (todo, categoryOpt)
      })

    slave.run(query)
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
