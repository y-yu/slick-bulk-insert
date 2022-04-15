package slick.bulkinsert

import cats.data.State
import cats.syntax.all.*
import slick.bulkinsert.BulkInsertable
import slick.bulkinsert.BulkInsertableInstances
import java.sql.PreparedStatement
import scala.compiletime.*
import scala.deriving.*

trait BulkInsertableGenericInstances { self: BulkInsertableInstances =>
  inline implicit def derive[A]: BulkInsertable[A] =
    summonFrom {
      case x: BulkInsertable[A] =>
        x
      case _: Mirror.ProductOf[A] =>
        deriveProduct[A]
    }

  inline def deriveProduct[A](using inline a: Mirror.ProductOf[A]): BulkInsertable[A] = {
    val xs = deriveRec[a.MirroredElemTypes]
    productImpl[A](xs, a)
  }

  def iterator[T](p: T): Iterator[_] = p.asInstanceOf[Product].productIterator

  final def productImpl[A](xs: List[BulkInsertable[_]], mirrorA: Mirror.ProductOf[A]): BulkInsertable[A] =
    new BulkInsertable[A] {
      override def set(statement: PreparedStatement, a: A): State[Int, Unit] =
        (xs zip iterator(a)).traverse { case (x, a) =>
          x.asInstanceOf[BulkInsertable[Any]].set(statement, a)
        }.void

      def parameterLength: Int = xs.length
    }

  inline def deriveRec[T <: Tuple]: List[BulkInsertable[_]] =
    inline erasedValue[T] match {
      case _: EmptyTuple =>
        Nil
      case _: (t *: ts) =>
        derive[t] :: deriveRec[ts]
    }
}
