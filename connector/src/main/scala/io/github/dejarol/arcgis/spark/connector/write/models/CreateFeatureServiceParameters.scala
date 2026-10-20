package io.github.dejarol.arcgis.spark.connector.write.models

import io.github.dejarol.arcgis.spark.connector.core.models.SpatialReference

case class CreateFeatureServiceParameters(
                                           name: String,
                                           spatialReference: SpatialReference,
                                           maxRecordCount: Int
                                         ) {
}
