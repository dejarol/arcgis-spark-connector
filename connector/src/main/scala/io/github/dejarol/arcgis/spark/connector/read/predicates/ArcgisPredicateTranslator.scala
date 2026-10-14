package io.github.dejarol.arcgis.spark.connector.read.predicates

import org.apache.spark.sql.connector.expressions.filter.Predicate

/**
 * TODO
 */
object ArcgisPredicateTranslator {

  /**
   * TODO
   * @param predicate
   * @return
   */
  def translate(predicate: Predicate): Option[String] = {

    // [1] Match on predicate name
    predicate.name() match {

      // [1.1] NULL conditions
      case "IS_NULL" | "IS_NOT_NULL" => translateNullPredicate(
        predicate, predicate.name().equals("IS_NOT_NULL")
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
  private def translateNullPredicate(
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
}
