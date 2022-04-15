package slick

import slick.lifted.*
import scala.quoted.*

/** @note
  *   Copy and paste from [[https://github.com/slick/slick/pull/2187]].
  */
trait CustomTableQuery {
  inline def TableQuery[E <: AbstractTable[_]]: TableQuery[E] = ${ TableQueryImpl.applyExpr[E] }
}

object TableQueryImpl {

  /** Create a TableQuery for a table row class using an arbitrary constructor function. */
  def apply[E <: AbstractTable[_]](cons: Tag => E): TableQuery[E] =
    new TableQuery[E](cons)

  type Extract[E] = E match {
    case AbstractTable[t] => t
  }

  def applyExpr[E <: AbstractTable[_]](using
    quotes: Quotes,
    e: Type[E],
    ev: Type[slick.lifted.Tag => E]
  ): Expr[TableQuery[E]] = {
    import quotes.reflect.*
    val eTpe = TypeRepr.of(using e)
    val tagTpe = TypeRepr.of[Tag]
    val mt = MethodType(List("tag"))(_ => List(tagTpe), _ => eTpe)

    val cons = Lambda(
      Symbol.spliceOwner,
      mt,
      { (meth, tag) =>
        Select.overloaded(
          New(TypeIdent(eTpe.typeSymbol)),
          "<init>",
          List(),
          List(tag.head.asInstanceOf[Term])
        )
      }
    )

    '{ TableQueryImpl.apply[E](${ cons.asExprOf[Tag => E] }) }
  }
}
