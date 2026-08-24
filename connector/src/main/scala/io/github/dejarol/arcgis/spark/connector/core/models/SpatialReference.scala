package io.github.dejarol.arcgis.spark.connector.core.models

/**
 * TODO
 * @param wkid
 * @param latestWkid
 */
case class SpatialReference(
                             wkid: Option[Int],
                             latestWkid: Option[Int]
                           )
