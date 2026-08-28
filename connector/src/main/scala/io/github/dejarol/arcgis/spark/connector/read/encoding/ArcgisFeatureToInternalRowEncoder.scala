package io.github.dejarol.arcgis.spark.connector.read.encoding

import io.github.dejarol.arcgis.spark.connector.read.models.ArcgisFeature
import org.apache.spark.sql.catalyst.InternalRow

/**
 * Encodes an ArcGIS feature into a Spark InternalRow.
 *
 * @since 0.1.0
 */
trait ArcgisFeatureToInternalRowEncoder
  extends Serializable {

  /**
   * Encodes a feature into a Spark InternalRow.
   *
   * @param feature ArcGIS feature to encode
   * @return an InternalRow of attribute values, and geometry when included
   * @since 0.1.0
   */
  def apply(feature: ArcgisFeature): InternalRow
}
