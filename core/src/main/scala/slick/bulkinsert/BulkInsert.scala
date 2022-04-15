package slick.bulkinsert

import slick.jdbc.JdbcProfile
import cats.syntax.all.*

trait BulkInsert[A] {
  protected val profile: JdbcProfile

  import profile.api.*

  protected def tableQuery: TableQuery[? <: Table[A]]

  /** @param dms
    *   Data models
    * @return
    *   The number of data inserted
    */
  def bulkInsert(dms: Seq[A])(implicit
    bulkInsertable: BulkInsertable[A]
  ): DBIO[Int] = dms match {
    case Nil =>
      // In case of `Nil` we cannot inject placeholder of `INSERT` query
      // so return `0` without SQL execution.
      DBIO.successful(0)

    case h +: ts =>
      SimpleDBIO { session =>
        val placeholder = (1 to bulkInsertable.parameterLength).map(_ => "?").mkString("(", ",", ")")
        // The query returned by `arel.insertStatement` has a placeholder by default
        // so we use `until` to make `placeholders.length` be `dms.length - 1`.
        val placeholders = (1 until dms.length).map(_ => placeholder).mkString(",")
        val sql = s"${tableQuery.insertStatement}, $placeholders"
        val statement = session.connection.prepareStatement(sql)

        ts
          .foldLeft(bulkInsertable.set(statement, h)) { (acc, dm) =>
            bulkInsertable.set(statement, dm) >> acc
          }
          .run(1)
          .value

        statement.executeUpdate()
      }
  }
}
