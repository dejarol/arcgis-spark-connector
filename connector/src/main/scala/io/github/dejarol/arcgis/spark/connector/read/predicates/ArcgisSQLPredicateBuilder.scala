package io.github.dejarol.arcgis.spark.connector.read.predicates

import io.github.dejarol.arcgis.spark.connector.core.utils.{Strings, Time}
import org.apache.spark.sql.connector.expressions.filter.Predicate
import org.apache.spark.sql.connector.expressions.{Expression, Literal, NamedReference}
import org.apache.spark.sql.types.{StringType, TimestampType}

import java.time.format.DateTimeFormatter

/**
 * Converts Spark V2 filter expressions into ArcGIS SQL where-clause fragments.
 *
 * @since 0.3.0
 */
object ArcgisSQLPredicateBuilder {

  /**
   * Builds an ArcGIS SQL fragment for a Spark filter expression, when supported.
   *
   * @param exp Spark expression to convert
   * @return the SQL fragment, or `None` if the expression cannot be pushed down
   * @since 0.3.0
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
   * Builds the dotted field name of a named reference.
   *
   * @param ref Spark named reference to convert
   * @return the field name, joined with dots when nested
   * @since 0.3.0
   */
  private def buildNamedReference(ref: NamedReference): Option[String] = {

    Some(
      ref.fieldNames().mkString(".")
    )
  }

  /**
   * Builds the SQL representation of a Spark literal.
   *
   * Strings are single-quoted and timestamps are formatted as UTC `TIMESTAMP` literals.
   *
   * @param lit Spark literal to convert
   * @return the SQL literal fragment
   * @since 0.3.0
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
   * Builds an ArcGIS SQL fragment for a Spark V2 predicate, when supported.
   *
   * @param predicate Spark predicate to convert
   * @return the SQL fragment, or `None` if the predicate cannot be pushed down
   * @since 0.3.0
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

      // [1.5] AND, OR
      case "AND" | "OR" => buildLogicalConjunctionPredicate(predicate, predicate.name().equals("AND"))
      case _ => None
    }
  }

  /**
   * Builds an `IS NULL` or `IS NOT NULL` SQL fragment for a field.
   *
   * @param predicate Spark null-check predicate
   * @param negate    `true` for `IS NOT NULL`, `false` for `IS NULL`
   * @return the SQL fragment, or `None` if the field name cannot be resolved
   * @since 0.3.0
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
   * Builds a binary comparison SQL fragment from a Spark predicate.
   *
   * @param predicate Spark comparison predicate
   * @param name      comparison operator (`=`, `<>`, `<`, `<=`, `>`, `>=`)
   * @return the SQL fragment, or `None` if either side cannot be converted
   * @since 0.3.0
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
   * Builds an `IN` SQL fragment from a Spark predicate.
   *
   * @param predicate Spark `IN` predicate
   * @return the SQL fragment, or `None` if the column or any literal cannot be converted
   * @since 0.3.0
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
   * Builds a `NOT` SQL fragment from a Spark predicate.
   *
   * @param predicate Spark `NOT` predicate
   * @return the SQL fragment, or `None` if the child expression cannot be converted
   * @since 0.3.0
   */
  private def buildNotPredicate(predicate: Predicate): Option[String] = {

    for {
      child <- predicate.children().headOption
      resolved <- build(child)
    } yield f"not ${Strings.withParenthesis(resolved)}"
  }

  /**
   * Builds an `AND` or `OR` SQL fragment from a Spark predicate.
   *
   * @param predicate Spark logical conjunction predicate
   * @param isAnd     `true` for `AND`, `false` for `OR`
   * @return the SQL fragment, or `None` if either side cannot be converted
   * @since 0.3.0
   */
  private def buildLogicalConjunctionPredicate(
                                                predicate: Predicate,
                                                isAnd: Boolean
                                              ): Option[String] = {
    val operator = if (isAnd) "and" else "or"
    val children = predicate.children()
    for {
      leftSide <- children.headOption
      rightSide <- children.tail.headOption
      leftStr <- build(leftSide)
      rightStr <- build(rightSide)
    } yield f"${Strings.withParenthesis(leftStr)} $operator ${Strings.withParenthesis(rightStr)}"
  }
}
