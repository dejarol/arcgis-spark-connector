package io.github.dejarol.arcgis.spark.connector.core

/**
 * Trait for types that should define their 'empty' state.
 *
 * @since 0.1.0
 */
trait EmptyOrNonEmpty {

  /**
   * Reports whether this instance is empty.
   *
   * @return `true` if this instance is empty
   * @since 0.1.0
   */
  def isEmpty: Boolean

  /**
   * Reports whether this instance is non-empty.
   *
   * @return `true` if this instance is not empty
   * @since 0.1.0
   */
  final def nonEmpty: Boolean = !isEmpty

}
