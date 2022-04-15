package slick.bulkinsert

import org.scalatest.BeforeAndAfterAll
import org.scalatest.BeforeAndAfterEach
import org.scalatest.diagrams.Diagrams
import org.scalatest.flatspec.AnyFlatSpec
import slick.bulkinsert.UserDataModel.createDataModels
import java.sql.SQLIntegrityConstraintViolationException

class BulkInsertTest extends AnyFlatSpec with Diagrams with BeforeAndAfterEach with BeforeAndAfterAll {
  override def beforeAll(): Unit = {
    super.beforeAll()
    UserTestDAO.createTable()
  }

  override def afterAll(): Unit = {
    UserTestDAO.dropTableIfExists()
    super.afterAll()
  }

  override def beforeEach(): Unit =
    UserTestDAO.delete()

  "bulkInsert" should "insert all data models successfully" in {
    val dms = createDataModels(10)

    assert(UserTestDAO.findAll === Nil)
    val actual = UserTestDAO.run(UserTestDAO.bulkInsert(dms))

    assert(actual === dms.length)
    assert(UserTestDAO.findAll === dms)
  }

  it should "try to insert `Nil` successfully" in {
    val dms = Seq.empty[UserDataModel]

    val actual = UserTestDAO.run(UserTestDAO.bulkInsert(dms))

    assert(actual === 0)
    assert(UserTestDAO.findAll === Nil)
  }

  it should "insert 100000 data successfully without stack overflow" in {
    val expectedSize = 100000
    val dms = createDataModels(expectedSize)

    val actual = UserTestDAO.run(UserTestDAO.bulkInsert(dms))

    assert(actual === expectedSize)
    assert(UserTestDAO.findAll === dms)
  }

  it should "fail if the data models contains duplicated IDs" in {
    val dms = createDataModels(10)

    val duplicatedIdDms = dms :+ dms.head

    intercept[SQLIntegrityConstraintViolationException] {
      UserTestDAO.run(UserTestDAO.bulkInsert(duplicatedIdDms))
    }
    assert(UserTestDAO.findAll === Nil)
  }
}
