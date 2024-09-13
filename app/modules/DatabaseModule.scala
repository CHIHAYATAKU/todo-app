package modules

import javax.inject.{ Inject, Provider, Singleton }
import scala.concurrent.Future
import com.google.inject.name.Names
import com.google.inject.AbstractModule
import play.api.inject.ApplicationLifecycle
import ixias.slick.model.DataSourceName
import ixias.slick.builder._
import ixias.slick.jdbc.MySQLProfile.api.Database

class DatabaseModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[Database])
      .annotatedWith(Names.named("master"))
      .toProvider(classOf[MasterDatabaseProvider])
      .asEagerSingleton()
    bind(classOf[Database])
      .annotatedWith(Names.named("slave"))
      .toProvider(classOf[SlaveDatabaseProvider])
      .asEagerSingleton()
  }
}