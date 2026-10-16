package io.github.dejarol.arcgis.spark.connector.read.predicates

import org.apache.spark.sql.connector.expressions.filter.Predicate
import org.apache.spark.sql.connector.expressions.{Expression, Literal, NamedReference}
import org.apache.spark.sql.types.StringType

/**
 * TODO
 */
object ArcgisSQLPredicateBuilder {

  def build(exp: Expression): Option[String] = {

    exp match {
      case n: NamedReference => buildNamedReference(n)
      case l: Literal[_] => buildLiteral(l)
      case p: Predicate => buildPredicate(p)
      case _ => None
    }
  }

  private def buildNamedReference(ref: NamedReference): Option[String] = {

    Some(
      ref.fieldNames().mkString(".")
    )
  }

  private def buildLiteral(lit: Literal[_]): Option[String] = {

    lit.dataType() match {
      case StringType => Some(f"'${String.valueOf(lit.value())}'")
      case _ => Some(String.valueOf(lit.value()))
    }
  }

  /**
   * TODO
   * @param predicate
   * @return
   */
  private def buildPredicate(predicate: Predicate): Option[String] = {

    // [1] Match on predicate name
    predicate.name() match {

      // [1.1] NULL conditions
      case "IS_NULL" | "IS_NOT_NULL" => buildNullPredicate(
        predicate, predicate.name().equals("IS_NOT_NULL")
      )

      // [1.2] Comparisons
      case "=" | "<>" | "<" | "<=" | ">" | ">=" => buildBinaryComparisonPredicate(
        predicate, predicate.name()
      )

      case _ => None
    }
  }

  /**
   * TODO
   * @param predicate
   * @param negate
   * @return
   */
  private def buildNullPredicate(
                                      predicate: Predicate,
                                      negate: Boolean
                                    ): Option[String] = {

    for {
      firstExpression <- predicate.children().headOption
      firstNamedReference <- firstExpression.references().headOption
      fieldName <- firstNamedReference.fieldNames().headOption
    } yield if (negate) {
      f"$fieldName is not null"
    } else {
      f"$fieldName is null"
    }
  }

  private def buildBinaryComparisonPredicate(
                                              predicate: Predicate,
                                              name: String
                                            ): Option[String] = {

    val children = predicate.children()
    val (maybeLeft, maybeRight) = (children.headOption, children.tail.headOption)
    for {
      left <- maybeLeft
      right <- maybeRight
      leftSQL <- build(left)
      rightSQL <- build(right)
    } yield f"$leftSQL $name $rightSQL"
  }
}
