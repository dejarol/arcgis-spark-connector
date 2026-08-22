package io.github.dejarol.arcgis.spark.connector.core.http.item

import io.github.dejarol.arcgis.spark.connector.core.EsriGeometryType

case class FeatureLayerDefinition(
                                   id: Int,
                                   name: String,
                                   `type`: String,
                                   geometryType: EsriGeometryType
                                 )

case class FeatureServiceDefinition(layers: Seq[FeatureLayerDefinition])
