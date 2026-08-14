package io.github.dejarol.arcgis.spark.connector.core.http

/**
 * TODO
 */
trait SttpRequestBuilder {

  /**
   * TODO
   * @param initial
   * @return
   */
  def build(initial: PRType): RType

}
