package io.github.dejarol.arcgis.spark.connector.read.models

import io.github.dejarol.arcgis.spark.connector.core.EmptyOrNonEmpty
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
                          fields: Option[Seq[FeatureLayerField]],
                          features: Seq[ArcgisFeature]
                        )
  extends EmptyOrNonEmpty {

  /**
   * TODO
   * @return
   */
  def isEmpty: Boolean = features.isEmpty && fields.isEmpty

  /**
   * TODO
   * @return
   */
  def fieldNames: Seq[String] = {

    fields.map {
      elements => elements.map(_.name)
    }.getOrElse(Seq.empty)
  }
}
