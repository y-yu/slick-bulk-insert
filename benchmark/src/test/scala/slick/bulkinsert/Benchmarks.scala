package slick.bulkinsert

import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations.*
import slick.bulkinsert.UserDataModel.createDataModels

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.SingleShotTime))
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2)
@Measurement(iterations = 10)
@Fork(value = 1, warmups = 1)
class Benchmarks {
  val num = 10000
  val dms: Seq[UserDataModel] = createDataModels(num)

  @Setup(Level.Trial)
  def setupTrial(): Unit = {
    UserTestDAO.dropTableIfExists()
    UserTestDAO.createTable()
  }

  @TearDown(Level.Iteration)
  def tearIteration(): Unit = {
    UserTestDAO.delete()
  }

  @Benchmark
  def benchSlickInsertAllJmh(): Unit = {
    UserTestDAO.addAll(dms)
  }

  @Benchmark
  def benchBulkInsertJmh(): Unit = {
    UserTestDAO.run(UserTestDAO.bulkInsert(dms))
  }
}
