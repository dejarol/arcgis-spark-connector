package io.github.dejarol.arcgis.spark.connector.read.models

/**
 * Body of an ArcGIS feature layer count-only query response.
 *
 * @param count number of features matching the query
 * @since 0.1.0
 */
case class ReturnCountOnlyResponse(count: Int)
