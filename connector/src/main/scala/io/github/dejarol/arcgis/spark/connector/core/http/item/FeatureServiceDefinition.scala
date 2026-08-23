package io.github.dejarol.arcgis.spark.connector.core.http.item

/**
 * Summarizes a layer contained in an ArcGIS feature service.
 *
 * @param id     layer identifier within the service
 * @param name   layer name
 * @param `type` ArcGIS layer type
 */
case class ShortLayerDefinition(
                                 id: Int,
                                 name: String,
                                 `type`: String
                               )

/**
 * Describes the layers exposed by an ArcGIS feature service.
 *
 * @param layers summaries of the layers in the service
 */
case class FeatureServiceDefinition(layers: Seq[ShortLayerDefinition]) {

  /**
   * Indexes the service layers by a key derived from each layer.
   *
   * @param keyFunction function that derives a key from a layer
   * @tparam K type of the derived key
   * @return the layers indexed by their derived keys
   */
  private def layersByKey[K](keyFunction: ShortLayerDefinition => K): Map[K, ShortLayerDefinition] = {

    layers.map {
      layer => (keyFunction(layer), layer)
    }.toMap
  }

  private lazy val layersById: Map[Int, ShortLayerDefinition] = layersByKey(_.id)
  private lazy val layersByName: Map[String, ShortLayerDefinition] = layersByKey(_.name)
}
