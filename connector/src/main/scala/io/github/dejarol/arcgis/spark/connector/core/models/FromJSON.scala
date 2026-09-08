package io.github.dejarol.arcgis.spark.connector.core.models

import org.json4s.JValue

/**
 * Trait for creating instances of a type from a JSON value.
 *
 * @tparam T the trait type
 * @since 0.1.0
 */
trait FromJSON[T] {

  /**
   * Creates an instance of this trait's type from a JSON value.
   *
   * @param json the input JSON value
   * @return an instance of this trait's type
   * @since 0.1.0
   */
  def fromJSON(json: JValue): T

  /**
   * Creates an IllegalArgumentException (to be thrown later on)
   * with a message indicating invalid JSON for a geometry type.
   *
   * @param `type` the geometry type
   * @since 0.1.0
   */
  protected final def exceptionForGeometryType(`type`: EsriGeometryType): IllegalArgumentException = {

    new IllegalArgumentException(
      f"Invalid json for '${`type`}' geometry type"
    )
  }
}
