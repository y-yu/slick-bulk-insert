package slick.bulkinsert

import cats.data.State
import java.sql.PreparedStatement

/** Representation that the type [[A]] is bulk insertable.
  */
trait BulkInsertable[A] {
  def set(statement: PreparedStatement, a: A): State[Int, Unit]

  /** @return
    *   The number of column of [[A]]
    */
  def parameterLength: Int
}

object BulkInsertable extends BulkInsertableInstances
