package io.github.dejarol.arcgis.spark.connector.read.models

import io.github.dejarol.arcgis.spark.connector.core.models.{EsriGeometryType, FeatureLayerField, SpatialReference}

/**
 * TODO
 * @param geometryType
 * @param spatialReference
 * @param fields
 * @param features
 */
case class QueryResponse(
                          geometryType: EsriGeometryType,
                          spatialReference: SpatialReference,
                          fields: Seq[FeatureLayerField],
                          features: Seq[Feature]
                        ) {

  /**
   * TODO
   * @return
   */
  def fieldNames: Seq[String] = fields.map(_.name)
}
