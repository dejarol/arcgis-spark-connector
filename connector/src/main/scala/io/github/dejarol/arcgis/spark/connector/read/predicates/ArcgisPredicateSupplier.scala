package io.github.dejarol.arcgis.spark.connector.read.predicates

/**
 * TODO
 */
trait ArcgisPredicateSupplier {

  /**
   * TODO
   * @return
   */
  def predicate(): String
}
