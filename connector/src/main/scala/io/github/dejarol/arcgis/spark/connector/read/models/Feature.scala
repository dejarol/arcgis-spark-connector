package io.github.dejarol.arcgis.spark.connector.read.models

import io.github.dejarol.arcgis.spark.connector.core.models.Geometry

/**
 * TODO
 * @param attributes
 * @param geometry
 */
case class Feature(
                    attributes: Map[String, Any],
                    geometry: Option[Geometry]
                  ) {

  /**
   * TODO
   * @return
   */
  def attributesKeys: Set[String] = attributes.keys.toSet
}
