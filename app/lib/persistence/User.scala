/**
  * This is a sample of Todo Application.
  * 
  */

package lib.persistence

import com.zaxxer.hikari.HikariDataSource

import scala.concurrent.{ExecutionContext, Future}
import ixias.model._
import ixias.slick.SlickRepository
import ixias.slick.builder.{DatabaseBuilder, HikariConfigBuilder}
import ixias.slick.jdbc.MySQLProfile.api._
import ixias.slick.model.DataSourceName
import lib.model.User
import lib.model.User.Id
import lib.persistence.db.UserTable
import slick.dbio.Effect
import slick.sql.FixedSqlAction

// UserRepository: UserTableへのクエリ発行を行うRepository層の定義
//~~~~~~~~~~~~~~~~~~~~~~
class UserRepository()(implicit val ec: ExecutionContext) extends SlickRepository[User.Id, User] {
  val master: Database = DatabaseBuilder.fromHikariDataSource(
    new HikariDataSource(HikariConfigBuilder.default(DataSourceName("ixias.db.mysql://master/user")).build())
  )
  val slave: Database = DatabaseBuilder.fromHikariDataSource(
    new HikariDataSource(HikariConfigBuilder.default(DataSourceName("ixias.db.mysql://slave/user")).build())
  )

  val userTable = TableQuery[UserTable]

  /**
    * Get User Data
    */
  def getById(id: User.Id): Future[Option[User]] = {
    slave.run(userTable.filter(_.id === id).result.headOption)
  }

  /**
    * Add User Data
   */
  def add(user: User#WithNoId): Future[User.Id] = {
    master.run(userTable returning userTable.map(_.id) += user.v)
  }

  /**
   * Update User Data
   */
  def update(entity: User#EmbeddedId): Future[Option[User#EmbeddedId]] = {
    master.run {
      userTable.filter(_.id === entity.id).update(entity.v).map(_ > 0).map {
        case true  => Some(entity)
        case false => None
      }
    }
  }

  /**
   * Delete User Data
   */
  def remove(id: User.Id): Future[Option[User#EmbeddedId]] = {
    master.run {
      userTable.filter(_.id === id).delete.map {
        case 0 => None
        case _ => Some(id.asInstanceOf[User#EmbeddedId])
      }
    }
  }
}