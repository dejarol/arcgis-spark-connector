package io.github.dejarol.arcgis.spark.connector.read.encoding

import io.github.dejarol.arcgis.spark.connector.core.models.Geometry
import org.apache.spark.sql.catalyst.InternalRow

/**
 * Converts a value from an ArcGIS REST API representation into a Spark-compatible type.
 *
 * @since 0.1.0
 */
trait ArcgisAPIValueEncoder[Input, Output] {

  /**
   * Encodes an ArcGIS API value into its Spark representation.
   *
   * @param value value to encode
   * @return the encoded Spark value
   * @since 0.1.0
   */
  def apply(value: Input): Output
}

/**
 * Encoder for optional ArcGIS feature attribute values.
 *
 * @since 0.1.0
 */
trait AttributeValueEncoder[Output]
  extends ArcgisAPIValueEncoder[Option[Any], Output]

/**
 * Encoder for ArcGIS geometry values, producing Spark InternalRows.
 *
 * @since 0.1.0
 */
trait GeometryValueEncoder[G <: Geometry]
  extends ArcgisAPIValueEncoder[G, InternalRow]
