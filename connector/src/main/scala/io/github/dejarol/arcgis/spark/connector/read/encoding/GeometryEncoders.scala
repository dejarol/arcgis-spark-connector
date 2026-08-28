package io.github.dejarol.arcgis.spark.connector.read.encoding

import io.github.dejarol.arcgis.spark.connector.core.models.{PointGeometry, PolygonGeometry, SpatialReference}
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.util.ArrayData

import java.lang

/**
 * Factory for encoders that convert ArcGIS geometry models into Spark InternalRows.
 *
 * @since 0.1.0
 */
object GeometryEncoders {

  /**
   * Encodes an ArcGIS spatial reference as a Spark InternalRow.
   *
   * @since 0.1.0
   */
  private object SpatialReferenceEncoder
    extends ArcgisAPIValueEncoder[SpatialReference, InternalRow] {

    /**
     * Encodes a spatial reference into an InternalRow of WKID and latest WKID.
     *
     * @param value spatial reference to encode
     * @return an InternalRow holding `wkid` and `latestWkid`, using nulls for missing values
     * @since 0.1.0
     */
    override def apply(value: SpatialReference): InternalRow = {

      InternalRow(
        value.wkid.orNull,
        value.latestWkid.orNull
      )
    }
  }

  /**
   * Encodes an ArcGIS point geometry as a Spark InternalRow.
   *
   * @since 0.1.0
   */
  private object PointEncoder
    extends GeometryValueEncoder[PointGeometry] {

    /**
     * Encodes a point into an InternalRow of x, y, and spatial reference.
     *
     * @param value point geometry to encode
     * @return an InternalRow holding coordinates and an optional encoded spatial reference
     * @since 0.1.0
     */
    override def apply(value: PointGeometry): InternalRow = {

      InternalRow(
        lang.Double.valueOf(value.x),
        lang.Double.valueOf(value.y),
        value.spatialReference.map(SpatialReferenceEncoder.apply).orNull
      )
    }
  }

  /**
   * Encodes an ArcGIS polygon geometry as a Spark InternalRow.
   *
   * @since 0.1.0
   */
  private object PolygonEncoder
    extends GeometryValueEncoder[PolygonGeometry] {

    /**
     * Encodes a polygon into an InternalRow of rings and spatial reference.
     *
     * @param value polygon geometry to encode
     * @return an InternalRow holding nested ring coordinates and an optional encoded spatial reference
     * @since 0.1.0
     */
    override def apply(value: PolygonGeometry): InternalRow = {

      InternalRow(
        createRingsArray(value.rings),
        value.spatialReference.map(SpatialReferenceEncoder.apply).orNull
      )
    }

    /**
     * Converts polygon rings into Spark ArrayData.
     *
     * @param rings nested ring coordinates (polygons of points of x/y values)
     * @return Spark array data matching the polygon rings schema
     * @since 0.1.0
     */
    private def createRingsArray(rings: Seq[Seq[Seq[Double]]]): ArrayData = {

      ArrayData.toArrayData(
        rings.map {
          polygon => ArrayData.toArrayData(
            polygon.map {
              point => ArrayData.toArrayData(
                point.map(lang.Double.valueOf)
              )
            }
          )
        }
      )
    }
  }

  /**
   * Returns the encoder for ArcGIS spatial references.
   *
   * @return an encoder that writes WKID and latest WKID into an InternalRow
   * @since 0.1.0
   */
  def forSpatialReference(): ArcgisAPIValueEncoder[SpatialReference, InternalRow] = SpatialReferenceEncoder

  /**
   * Returns the encoder for ArcGIS point geometries.
   *
   * @return an encoder that writes x, y, and spatial reference into an InternalRow
   * @since 0.1.0
   */
  def forPoints(): GeometryValueEncoder[PointGeometry] = PointEncoder

  /**
   * Returns the encoder for ArcGIS polygon geometries.
   *
   * @return an encoder that writes rings and spatial reference into an InternalRow
   * @since 0.1.0
   */
  def forPolygons(): GeometryValueEncoder[PolygonGeometry] = PolygonEncoder
}
