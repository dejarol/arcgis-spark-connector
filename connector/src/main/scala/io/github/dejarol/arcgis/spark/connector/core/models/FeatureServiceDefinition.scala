package io.github.dejarol.arcgis.spark.connector.core.models

/**
 * TODO
 * @param layers
 */
case class FeatureServiceDefinition(layers: Seq[ShortLayerDefinition]) {

  /**
   * TODO
   * @param keyFunction
   * @tparam K
   * @return
   */

  private def layersByKey[K](keyFunction: ShortLayerDefinition => K): Map[K, ShortLayerDefinition] = {

    layers.map {
      layer => (keyFunction(layer), layer)
    }.toMap
  }

  private lazy val layersById: Map[Int, ShortLayerDefinition] = layersByKey(_.id)

  private lazy val layersByName: Map[String, ShortLayerDefinition] = layersByKey(_.name)
}
