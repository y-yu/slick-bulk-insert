package slick.bulkinsert

import java.sql.PreparedStatement
import cats.data.State
import cats.syntax.all.*
import slick.bulkinsert.BulkInsertable
import java.sql.PreparedStatement
import shapeless3.deriving.*

abstract class AutoDerivedBulkInsertable[A] extends BulkInsertable[A]

object AutoDerivedBulkInsertable {
  implicit def bulkInsertableGenInstance[A](implicit inst: K0.ProductInstances[BulkInsertable, A]): AutoDerivedBulkInsertable[A] =
    new AutoDerivedBulkInsertable[A] {
      def set(statement: PreparedStatement, a: A): State[Int, Unit] = {
        inst.foldLeft(a)(State(s => (s, ())): State[Int, Unit]) {
          [t] => (acc: State[Int, Unit], bk: BulkInsertable[t], x: t) =>
            acc >> bk.set(statement, x)
        }
      }

      def parameterLength: Int = inst.unfold(0) {
        [t] => (acc: Int, bk: BulkInsertable[t]) =>
          // The second value of this tuple is never used so it's safe for now.
          (acc + bk.parameterLength, Some(null.asInstanceOf[t]))
      }._1
    }
}
