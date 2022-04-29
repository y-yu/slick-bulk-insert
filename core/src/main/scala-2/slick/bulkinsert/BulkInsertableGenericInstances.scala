package slick.bulkinsert

import cats.data.State
import java.sql.PreparedStatement
import shapeless.*
import slick.bulkinsert.BulkInsertable
import slick.bulkinsert.BulkInsertableInstances

trait BulkInsertableGenericInstances { self: BulkInsertableInstances =>
  implicit val hNilInstance: BulkInsertable[HNil] = new BulkInsertable[HNil] {
    def set(statement: PreparedStatement, a: HNil): State[Int, Unit] =
      State(s => (s, ()))

    def parameterLength: Int = 0
  }

  implicit def hConsInstance[H, T <: HList](implicit
    head: BulkInsertable[H],
    tail: BulkInsertable[T]
  ): BulkInsertable[H :: T] = new BulkInsertable[H :: T] {
    def set(statement: PreparedStatement, a: H :: T): State[Int, Unit] =
      for {
        _ <- head.set(statement, a.head)
        _ <- tail.set(statement, a.tail)
      } yield ()

    def parameterLength: Int = head.parameterLength + tail.parameterLength
  }
}
