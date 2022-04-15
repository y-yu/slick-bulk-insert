Auto bulk `INSERT` query generator for Slick
=============================================

[![Test and Benchmark](https://github.com/y-yu/slick-bulk-insert/actions/workflows/ci.yml/badge.svg)](https://github.com/y-yu/slick-bulk-insert/actions/workflows/ci.yml)

Auto generation for low-level [Slick](https://scala-slick.org/) _bulk_ insertion query using [shapeless](https://github.com/milessabin/shapeless) and Scala 3 macro.

```scala
object UserDAO extends BulkInsert[User] {
  class UserTable extends Table[User] {
    // ....
  }
}

val users: Seq[User] = ???

UserDAO.bulkInsert(users)
```

## Benchmark

You can run the benchmark against Slick `++=` with `./sbt benchmark/Jmh/run`(only run in Scala 2).

```
[info] Benchmark                          Mode  Cnt     Score     Error  Units
[info] Benchmarks.benchBulkInsertJmh        ss   10   347.465 ±  94.048  ms/op
[info] Benchmarks.benchSlickInsertAllJmh    ss   10  3411.940 ± 123.495  ms/op
```

This bulk insertion is about 10 times faster than Slick `++=`.

## References

- [Slick 3.0 bulk insert or update (upsert)](https://stackoverflow.com/questions/35001493/slick-3-0-bulk-insert-or-update-upsert)
- [Slick（MySQL）でBulk Upsertを実装する](https://zenn.dev/taketora/articles/7ececc752eee2c)

## Acknowledgement

Thanks [@xuwei-k](https://twitter.com/xuwei_k) about Scala 3 macros and Slick `SetParameter`.   