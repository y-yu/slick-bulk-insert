package slick.bulkinsert

import org.scalatest.diagrams.Diagrams
import org.scalatest.flatspec.AnyFlatSpec

class BulkInsertableTest extends AnyFlatSpec with Diagrams {
  case class Dummy1(
    a: Int,
    b: Double,
    c: String,
    d: Boolean
  )

  "BulkInsertable.semiauto" should "derive an instance of `Dummy1`" in {
    val actual = BulkInsertable.semiauto[Dummy1]
    assert(actual.parameterLength === 4)
  }

  it should "derive an instance of `Dummy1` using type inference without a type parameter" in {
    val actual: BulkInsertable[Dummy1] = BulkInsertable.semiauto
    assert(actual.parameterLength === 4)
  }

  "AutoDerivedBulkInsertable" should "derive an instance of `Dummy1` only `import`" in {
    import AutoDerivedBulkInsertable.*
    val actual = implicitly[BulkInsertable[Dummy1]]

    assert(actual.parameterLength === 4)
  }
}
