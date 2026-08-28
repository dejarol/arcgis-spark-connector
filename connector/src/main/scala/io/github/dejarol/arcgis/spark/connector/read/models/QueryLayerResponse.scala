package io.github.dejarol.arcgis.spark.connector.read.models

import io.github.dejarol.arcgis.spark.connector.core.EmptyOrNonEmpty
import io.github.dejarol.arcgis.spark.connector.core.models.{EsriGeometryType, FeatureLayerField, SpatialReference}

/**
 * Body of an ArcGIS feature layer query response.
 *
 * @param geometryType     geometry type of the returned features
 * @param spatialReference spatial reference of the returned geometries
 * @param fields           optional field definitions included with the response
 * @param features         features returned by the query
 * @since 0.1.0
 */
case class QueryLayerResponse(
                               geometryType: EsriGeometryType,
                               spatialReference: SpatialReference,
                               fields: Option[Seq[FeatureLayerField]],
                               features: Seq[ArcgisFeature]
                             )
  extends EmptyOrNonEmpty {

  /**
   * Reports whether this response contains no features and no fields.
   *
   * @return `true` if both `features` and `fields` are empty
   * @since 0.1.0
   */
  def isEmpty: Boolean = features.isEmpty && fields.isEmpty

  /**
   * Returns the names of the fields included with this response.
   *
   * @return field names, or an empty sequence when `fields` is absent
   * @since 0.1.0
   */
  def fieldNames: Seq[String] = {

    fields.map {
      elements => elements.map(_.name)
    }.getOrElse(Seq.empty)
  }
}
