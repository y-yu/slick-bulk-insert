import ReleaseTransformations._

import sbt._
import Keys._
import org.scalafmt.sbt.ScalafmtPlugin.autoImport._

val scala213 = "2.13.8"
val scala3 = "3.1.2"

val isScala3 = Def.setting(
  CrossVersion.partialVersion(scalaVersion.value).exists(_._1 == 3)
)

lazy val root =
  (project in file("."))
    .settings(noPublish: _*)
    .settings(
      addCommandAlias("SetScala3", s"++ $scala3!"),
      addCommandAlias("SetScala2", s"++ $scala213!")
    )
    .aggregate(
      core,
      benchmark
    )

lazy val benchmark =
  (project in file("benchmark"))
    .settings(DockerUtils.runMySQLSetting ++ baseOptions ++ noPublish: _*)
    .settings(
      name := "slick-bulk-insert-benchmark",
      // IntellJ IDEA doesn't support "compile->test" dependency so it workaround for the problem.
      Jmh / sourceDirectory := (Test / sourceDirectory).value,
      Jmh / classDirectory := (Test / classDirectory).value,
      Jmh / dependencyClasspath := (Test / dependencyClasspath).value,
      Jmh / compile := (Jmh / compile).dependsOn(Test / compile).value,
      Jmh / run := (Jmh / run).dependsOn(Jmh / Keys.compile).evaluated
    )
    .dependsOn(
      core % "test->test"
    )
    .enablePlugins(JmhPlugin)

lazy val core =
  (project in file("core"))
    .settings(DockerUtils.runMySQLSetting ++ baseOptions: _*)
    .settings(
      name := "slick-bulk-insert",
      description := "Supporting Slick bulk insertion using Scala macro",
      libraryDependencies ++= {
        if (scalaBinaryVersion.value == "3") {
          Seq("org.typelevel" %% "shapeless3-deriving" % "3.0.4")
        } else {
          Seq("com.chuusai" %% "shapeless" % "2.3.9")
        }
      },
      libraryDependencies ++= Seq(
        "com.typesafe.slick" %% "slick" % "3.3.3" cross CrossVersion.for3Use2_13,
        "org.typelevel" %% "cats-core" % "2.7.0",
        "mysql" % "mysql-connector-java" % "8.0.29" % "test",
        "org.slf4j" % "slf4j-nop" % "1.7.36" % "test",
        "org.scalatest" %% "scalatest" % "3.2.13" % "test"
      ),
      organization := "com.github.y-yu",
      homepage := Some(url("https://github.com/y-yu")),
      licenses := Seq("MIT" -> url(s"https://github.com/y-yu/slick-bulk-insert/blob/master/LICENSE")),
      scalafmtOnCompile := !isScala3.value,
      publishMavenStyle := true,
      publishTo := Some(
        if (isSnapshot.value)
          Opts.resolver.sonatypeSnapshots
        else
          Opts.resolver.sonatypeStaging
      ),
      Test / publishArtifact := false,
      pomExtra :=
        <developers>
          <developer>
            <id>y-yu</id>
            <name>Yoshimura Hikaru</name>
            <url>https://github.com/y-yu</url>
          </developer>
        </developers>
          <scm>
            <url>git@github.com:y-yu/slick-bulk-insert.git</url>
            <connection>scm:git:git@github.com:y-yu/slick-bulk-insert.git</connection>
            <tag>{tagOrHash.value}</tag>
          </scm>,
      releaseTagName := tagName.value,
      releaseCrossBuild := true,
      releaseProcess := Seq[ReleaseStep](
        checkSnapshotDependencies,
        inquireVersions,
        runClean,
        runTest,
        setReleaseVersion,
        commitReleaseVersion,
        tagRelease,
        releaseStepCommandAndRemaining("+publishSigned"),
        setNextVersion,
        commitNextVersion,
        releaseStepCommand("sonatypeReleaseAll"),
        pushChanges
      ),
      publishConfiguration := publishConfiguration.value.withOverwrite(true),
      publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)
    )

val baseOptions = Seq(
  scalaVersion := scala213,
  crossScalaVersions := Seq(scala213, scala3),
  scalacOptions ++= {
    if (isScala3.value) {
      Seq(
        "-source",
        "3.0-migration"
      )
    } else {
      Seq(
        "-Xlint:infer-any",
        "-Xsource:3",
        "-Ybackend-parallelism",
        "16"
      )
    }
  },
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding",
    "UTF-8",
    "-feature",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:existentials",
    "-unchecked"
  )
)

val noPublish = Seq(
  publishArtifact := false,
  publish := {},
  publishLocal := {},
  publish / skip := true
)

val tagName = Def.setting {
  s"v${if (releaseUseGlobalVersion.value) (ThisBuild / version).value else version.value}"
}

val tagOrHash = Def.setting {
  if (isSnapshot.value) sys.process.Process("git rev-parse HEAD").lineStream_!.head
  else tagName.value
}
