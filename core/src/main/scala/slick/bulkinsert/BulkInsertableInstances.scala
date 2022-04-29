package slick.bulkinsert

import cats.data.State
import slick.jdbc.PositionedParameters
import slick.jdbc.SetParameter
import java.sql.PreparedStatement

trait BulkInsertableInstances extends BulkInsertableGenericInstances {
  final def semiauto[A](implicit instance: AutoDerivedBulkInsertable[A]): BulkInsertable[A] =
    instance

  implicit def setParameterInstance[A](implicit
    setParameter: SetParameter[A]
  ): BulkInsertable[A] =
    new BulkInsertable[A] {
      def set(statement: PreparedStatement, a: A): State[Int, Unit] =
        State { s =>
          // Slick `PositionedParameters` manages the position of placeholder
          // but we don't use it.
          val positionedParameters = new PositionedParameters(statement)
          positionedParameters.pos = s - 1
          (s + 1, setParameter(a, positionedParameters))
        }

      def parameterLength: Int = 1
    }
}
