package io.github.dejarol.arcgis.spark.connector.core.models


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
