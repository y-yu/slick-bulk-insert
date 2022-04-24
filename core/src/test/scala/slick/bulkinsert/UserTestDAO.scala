package slick.bulkinsert

import slick.Scala3CompatTableQuery
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import java.sql.Date
import scala.concurrent.Await
import scala.concurrent.duration.*

object UserTestDAO extends BulkInsert[UserDataModel] with Scala3CompatTableQuery {
  private val databaseConfig: DatabaseConfig[JdbcProfile] =
    DatabaseConfig.forConfig("testMySQL")

  override protected val profile = databaseConfig.profile

  import profile.api.*

  override def tableQuery: TableQuery[UserTable] = TableQuery[UserTable]

  class UserTable(tag: Tag) extends Table[UserDataModel](tag, "users") {
    def id = column[Int]("id", O.PrimaryKey)

    def name = column[Option[String]]("name")

    def height = column[Double]("height")

    def weight = column[Double]("weight")

    def createdAt = column[Date]("created_at")

    def * =
      (id, name, height, weight, createdAt).<>(
        { case (id, name, height, weight, createdAt) =>
          UserDataModel(id, name, UserInfoDataModel(height, weight), createdAt)
        },
        (x: UserDataModel) => Option(x.id, x.name, x.info.height, x.info.weight, x.createdAt)
      )
  }

  def run[A](dbio: DBIO[A], timeout: FiniteDuration = 30.seconds): A =
    Await.result(databaseConfig.db.run(dbio.transactionally), timeout)

  def addAll(dms: Seq[UserDataModel]): Unit =
    run(tableQuery ++= dms)

  def findAll: Seq[UserDataModel] =
    run(tableQuery.sortBy(_.id).result)

  def delete(): Unit =
    run(tableQuery.delete)

  def createTable(): Unit =
    run(tableQuery.schema.create)

  def dropTableIfExists(): Unit =
    run(tableQuery.schema.dropIfExists)
}
