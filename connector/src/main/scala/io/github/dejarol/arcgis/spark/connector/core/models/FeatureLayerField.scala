package io.github.dejarol.arcgis.spark.connector.core.models

/**
 * Describes a field exposed by an ArcGIS feature layer.
 *
 * @param name   field name
 * @param `type` ArcGIS field data type
 * @since 0.1.0
 */
case class FeatureLayerField(
                              name: String,
                              `type`: EsriFieldType
                            )
