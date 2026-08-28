package io.github.dejarol.arcgis.spark.connector.core.models

/**
 * ArcGIS spatial reference identified by well-known IDs.
 *
 * @param wkid       optional well-known ID of the spatial reference
 * @param latestWkid optional well-known ID of the latest equivalent spatial reference
 * @since 0.1.0
 */
case class SpatialReference(
                             wkid: Option[Int],
                             latestWkid: Option[Int]
                           )
