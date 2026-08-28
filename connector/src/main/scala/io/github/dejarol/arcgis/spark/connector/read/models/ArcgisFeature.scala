package io.github.dejarol.arcgis.spark.connector.read.models

import io.github.dejarol.arcgis.spark.connector.core.models.Geometry

/**
 * A single feature from an ArcGIS feature layer query.
 *
 * @param attributes attribute values keyed by field name
 * @param geometry   optional geometry of the feature
 * @since 0.1.0
 */
case class ArcgisFeature(
                          attributes: Map[String, Option[Any]],
                          geometry: Option[Geometry]
                        ) {

  /**
   * Looks up an attribute by field name.
   *
   * @param key attribute field name
   * @return `Some` wrapping the optional attribute value when `key` exists, `None` otherwise
   * @since 0.1.0
   */
  def getAttribute(key: String): Option[Option[Any]] = attributes.get(key)
}
