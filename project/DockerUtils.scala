import sbt._

import Keys._
import sbt.taskKey
import sbt.util.Logger
import sys.process._

object DockerUtils {
  private val waitingTime = 2000 // milli seconds

  val runMySQL = taskKey[Unit]("Run MySQL in Docker")

  val runMySQLSetting = Seq(
    runMySQL := {
      runLocalMySQL(streams.value.log)
    },
    Test / test := (Test / test).dependsOn(DockerUtils.runMySQL).value,
    Test / testOnly := (Test / testOnly).dependsOn(DockerUtils.runMySQL).evaluated
  )

  private def runLocalMySQL(
    log: Logger
  ): Unit = {
    val command = List(
      "docker-compose",
      "up",
      "--detach"
    ).mkString(" ")

    try {
      command.lineStream(log)

      log.info("Launch the MySQL in Docker!")
      log.info(s"Wait ${waitingTime / 1000} seconds until MySQL is up.")
      Thread.sleep(waitingTime)
    } catch {
      case _: Throwable =>
        sys.error("Fail to run MySQL")
    }
  }

}
