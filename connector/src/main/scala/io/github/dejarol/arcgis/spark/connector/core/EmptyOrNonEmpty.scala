package io.github.dejarol.arcgis.spark.connector.core

/**
 * Trait for types that should define their 'empty' state.
 *
 * @since 0.1.0
 */
trait EmptyOrNonEmpty {

  /**
   * TODO
   * @return
   */
  def isEmpty: Boolean

  /**
   * TODO
   * @return
   */
  final def nonEmpty: Boolean = !isEmpty

}
