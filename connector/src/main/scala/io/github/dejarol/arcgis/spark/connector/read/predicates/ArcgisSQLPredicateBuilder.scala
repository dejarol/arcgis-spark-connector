package io.github.dejarol.arcgis.spark.connector.read.predicates

import io.github.dejarol.arcgis.spark.connector.core.utils.{Strings, Time}
import org.apache.spark.sql.connector.expressions.filter.Predicate
import org.apache.spark.sql.connector.expressions.{Expression, Literal, NamedReference}
import org.apache.spark.sql.types.{StringType, TimestampType}

import java.time.format.DateTimeFormatter

/**
 * TODO
 */
object ArcgisSQLPredicateBuilder {

  /**
   * TODO
   * @param exp
   * @return
   */
  def build(exp: Expression): Option[String] = {

    exp match {
      case n: NamedReference => buildNamedReference(n)
      case l: Literal[_] => buildLiteral(l)
      case p: Predicate => buildPredicate(p)
      case _ => None
    }
  }

  /**
   * TODO
   * @param ref
   * @return
   */
  private def buildNamedReference(ref: NamedReference): Option[String] = {

    Some(
      ref.fieldNames().mkString(".")
    )
  }

  /**
   * TODO
   * @param lit
   * @return
   */
  private def buildLiteral(lit: Literal[_]): Option[String] = {

    lit.dataType() match {

      // case String: quote literal value
      case StringType => Some(
        Strings.withSingleQuote(
          String.valueOf(lit.value())
        )
      )

      // case Timestamp: the literal value is a log expressing epoch microseconds
      // We convert the microSeconds to a UTC date and format it a string
      case TimestampType =>

        val formattedDate = Time.microsecondsToUTCDate(
          lit.value().asInstanceOf[Long]
        ).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        Some(s"TIMESTAMP ${Strings.withSingleQuote(formattedDate)}")
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

      // [1.1] NULL equality
      case "IS_NULL" | "IS_NOT_NULL" => buildNullPredicate(
        predicate, predicate.name().equals("IS_NOT_NULL")
      )

      // [1.2] Binary comparison
      case "=" | "<>" | "<" | "<=" | ">" | ">=" => buildBinaryComparisonPredicate(
        predicate, predicate.name()
      )

      // [1.3] IN clause
      case "IN" => buildInPredicate(predicate)

      // [1.4] NOT clause
      case "NOT" => buildNotPredicate(predicate)
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

  /**
   * TODO
   * @param predicate
   * @param name
   * @return
   */
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

  /**
   * TODO
   * @param predicate
   * @return
   */
  private def buildInPredicate(predicate: Predicate): Option[String] = {

    // We need to process
    // [1] source column name
    val children = predicate.children()
    val sourceColumnName = children.headOption.flatMap(build)

    // [2] literals within IN clause. We need to resolve all of them
    val expressionsOfInClause = children.tail
    val resolvedLiterals = if (expressionsOfInClause.isEmpty) {
      None
    } else {
      val maybeLiterals = expressionsOfInClause.map(build)
      if (maybeLiterals.forall(_.isDefined)) {

        // All literals were resolved, we can try to push it down
        Some(
          maybeLiterals.collect {
            case Some(value) => value
          }
        )
      } else {
        // Some literals were unresolved: don't push
        None
      }
    }

    // Lastly, build the final predicate if both column name and all literals were resolved
    for {
      name <- sourceColumnName
      literals <- resolvedLiterals
    } yield f"$name in (${literals.mkString(", ")})"
  }

  /**
   * TODO
   * @param predicate
   * @return
   */
  private def buildNotPredicate(predicate: Predicate): Option[String] = {

    for {
      child <- predicate.children().headOption
      resolved <- build(child)
    } yield f"not ${Strings.withParenthesis(resolved)}"
  }
}
