package io.github.dejarol.arcgis.spark.connector.read.models

import io.github.dejarol.arcgis.spark.connector.core.models.Geometry

/**
 * TODO
 * @param attributes
 * @param geometry
 */
case class ArcgisFeature(
                          attributes: Map[String, Option[Any]],
                          geometry: Option[Geometry]
                        ) {

  /**
   * TODO
   * @param key
   * @return
   */
  def containsAttribute(key: String): Boolean = attributes.contains(key)
}
