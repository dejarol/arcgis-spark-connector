package io.github.dejarol.arcgis.spark.connector.core.http.item

import io.github.dejarol.arcgis.spark.connector.core.EsriGeometryType

/**
 * TODO
 * @param id
 * @param name
 * @param `type`
 * @param geometryType
 */
case class FeatureLayerDefinition(
                                   id: Int,
                                   name: String,
                                   `type`: String,
                                   geometryType: EsriGeometryType
                                 )

/**
 * TODO
 * @param layers
 */
case class FeatureServiceDefinition(
                                     layers: Seq[FeatureLayerDefinition]
                                   ) {

  /**
   * TODO
   * @param keyFunction
   * @tparam K
   * @return
   */
  private def layersByKey[K](keyFunction: FeatureLayerDefinition => K): Map[K, FeatureLayerDefinition] = {

    layers.map {
      layer => (keyFunction(layer), layer)
    }.toMap
  }

  private lazy val layersById: Map[Int, FeatureLayerDefinition] = layersByKey(_.id)
  private lazy val layersByName: Map[String, FeatureLayerDefinition] = layersByKey(_.name)
}
