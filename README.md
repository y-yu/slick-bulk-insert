Auto bulk `INSERT` query generator for Slick
=============================================

[![Test and Benchmark](https://github.com/y-yu/slick-bulk-insert/workflows/CI/badge.svg)](https://github.com/y-yu/slick-bulk-insert/actions/workflows/ci.yml)

| Scala 2                                                                                                                                                                  | Scala 3                                                                                                                                                            |
|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [![Maven](https://img.shields.io/maven-central/v/com.github.y-yu/slick-bulk-insert_2.13.svg)](https://mvnrepository.com/artifact/com.github.y-yu/slick-bulk-insert_2.13) | [![Maven](https://img.shields.io/maven-central/v/com.github.y-yu/slick-bulk-insert_3.svg)](https://mvnrepository.com/artifact/com.github.y-yu/slick-bulk-insert_3) |

Auto generation for low-level [Slick](https://scala-slick.org/) _bulk_ insertion query using [shapeless](https://github.com/milessabin/shapeless) and [shapeless-3](https://github.com/typelevel/shapeless-3).

```scala
object UserDAO extends BulkInsert[User] {
  class UserTable extends Table[User] {
    // ....
  }
}

val users: Seq[User] = ???

UserDAO.bulkInsert(users)
```

It works on both Scala 2 and Scala 3.

## Getting started

```scala
libraryDependency += "com.github.y-yu" %% "slick-bulk-insert" % "<<version>>"
```

## Benchmark

You can run the benchmark against Slick `++=` with `./sbt benchmark/Jmh/run`.

- Scala 2
    ```
    [info] Benchmark                          Mode  Cnt     Score    Error  Units
    [info] Benchmarks.benchBulkInsertJmh        ss   10   303.221 ± 61.063  ms/op
    [info] Benchmarks.benchSlickInsertAllJmh    ss   10  2640.769 ± 74.003  ms/op
    ```
- Scala 3
    ```
    [info] Benchmark                          Mode  Cnt     Score     Error  Units
    [info] Benchmarks.benchBulkInsertJmh        ss   10   298.794 ±  55.171  ms/op
    [info] Benchmarks.benchSlickInsertAllJmh    ss   10  2827.452 ± 142.903  ms/op
    ```

This bulk insertion is about 10 times faster than Slick `++=`.

## References

- [Slick 3.0 bulk insert or update (upsert)](https://stackoverflow.com/questions/35001493/slick-3-0-bulk-insert-or-update-upsert)
- [Slick（MySQL）でBulk Upsertを実装する](https://zenn.dev/taketora/articles/7ececc752eee2c)

## Acknowledgement

Thanks [@xuwei-k](https://twitter.com/xuwei_k) about Scala 3 macros, Slick `SetParameter` and shapeless-3 information.   