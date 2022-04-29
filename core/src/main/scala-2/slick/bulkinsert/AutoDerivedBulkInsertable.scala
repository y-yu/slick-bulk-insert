package slick.bulkinsert

import cats.data.State
import shapeless.Generic
import shapeless.HList
import shapeless.Lazy
import java.sql.PreparedStatement

abstract class AutoDerivedBulkInsertable[A] extends BulkInsertable[A]

object AutoDerivedBulkInsertable {
  implicit def hListInstance[A, L <: HList](implicit
    gen: Generic.Aux[A, L],
    hList: Lazy[BulkInsertable[L]]
  ): AutoDerivedBulkInsertable[A] = new AutoDerivedBulkInsertable[A] {
    def set(statement: PreparedStatement, a: A): State[Int, Unit] =
      hList.value.set(statement, gen.to(a))

    def parameterLength: Int = hList.value.parameterLength
  }
}
