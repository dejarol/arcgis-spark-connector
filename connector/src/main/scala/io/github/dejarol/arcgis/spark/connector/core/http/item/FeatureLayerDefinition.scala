package io.github.dejarol.arcgis.spark.connector.core.http.item

import io.github.dejarol.arcgis.spark.connector.core.{EsriFieldType, EsriGeometryType}

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

/**
 * Describes the schema and query limits of an ArcGIS feature layer.
 *
 * @param id             layer identifier within its feature service
 * @param name           layer name
 * @param geometryType   geometry type stored by the layer
 * @param maxRecordCount maximum number of records returned per query
 * @param fields         fields exposed by the layer
 * @since 0.1.0
 */
case class FeatureLayerDefinition(
                                   id: Int,
                                   name: String,
                                   geometryType: EsriGeometryType,
                                   maxRecordCount: Int,
                                   fields: Seq[FeatureLayerField]
                                 )
