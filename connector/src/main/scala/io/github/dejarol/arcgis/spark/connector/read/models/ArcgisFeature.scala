package io.github.dejarol.arcgis.spark.connector.read.models

import io.github.dejarol.arcgis.spark.connector.core.models.Geometry
import org.json4s.JsonAST.JValue

/**
 * A single feature from an ArcGIS feature layer query.
 *
 * A feature consists of a
 *  - set of attributes, here modeled as a map where keys are field names and values are field values
 *  - an optional geometry
 * @param attributes the feature attributes
 * @param geometry   optional geometry of the feature
 * @since 0.1.0
 */
case class ArcgisFeature(
                          attributes: Map[String, JValue],
                          geometry: Option[Geometry]
                        ) {

  /**
   * Looks up an attribute by field name.
   *
   * @param key attribute field name
   * @return `Some` wrapping the optional attribute value when `key` exists, `None` otherwise
   * @since 0.1.0
   */
  def unsafelyGetAttribute(key: String): JValue = {

    attributes.get(key) match {
      case Some(value) => value
      case None => throw new IllegalStateException(
        f"Key $key does not exist within the attributes of this feature"
      )
    }
  }
}
