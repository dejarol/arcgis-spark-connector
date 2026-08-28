package io.github.dejarol.arcgis.spark.connector.read.encoding

import io.github.dejarol.arcgis.spark.connector.core.{BasicSpec, UnsupportedArcgisGeometryTypeException}
import io.github.dejarol.arcgis.spark.connector.core.models.{Geometry, PointGeometry, PolygonGeometry, SpatialReference}
import io.github.dejarol.arcgis.spark.connector.core.schema.SparkGeometryTypes
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.util.ArrayData

class GeometryEncodersSpec
  extends BasicSpec {

  /**
   * Asserts that a spatial reference is encoded with the expected WKID fields.
   *
   * @param sp spatial reference to encode and verify
   * @since 0.1.0
   */
  private def assertSpatialReferenceEncoding(sp: SpatialReference): Unit = {

    val row = GeometryEncoders.forSpatialReference().apply(sp)
    sp.wkid match {
      case Some(value) => row.getInt(0) shouldBe value
      case None => row.isNullAt(0) shouldBe true
    }

    sp.latestWkid match {
      case Some(value) => row.getInt(1) shouldBe value
      case None => row.isNullAt(1) shouldBe true
    }
  }

  /**
   * Asserts that a geometry's spatial reference is encoded at the expected InternalRow index.
   *
   * @param geometry geometry whose spatial reference is checked
   * @param row      encoded InternalRow produced for `geometry`
   * @throws UnsupportedArcgisGeometryTypeException if `geometry` is neither a point nor a polygon
   * @since 0.1.0
   */
  private def assertSpatialReferenceEncodingForGeometry(
                                                         geometry: Geometry,
                                                         row: InternalRow
                                                       ): Unit = {

    val indexForSR: Int = geometry match {
      case _: PointGeometry => 2
      case _: PolygonGeometry => 1
      case _ => throw new UnsupportedArcgisGeometryTypeException(geometry.`type`())
    }

    geometry.spatialReference match {
      case Some(_) =>

        // Assert that the spatial reference is encoded correctly
        row.isNullAt(indexForSR) shouldBe false
        row.get(indexForSR, SparkGeometryTypes.SPATIAL_REFERENCE_TYPE) shouldBe a [InternalRow]

      case None =>
        // Assert that the spatial reference is null
        row.isNullAt(indexForSR) shouldBe true
    }
  }

  /**
   * Asserts that a point geometry is encoded with the expected coordinates and spatial reference.
   *
   * @param point point geometry to encode and verify
   * @since 0.1.0
   */
  private def assertPointEncoding(point: PointGeometry): Unit = {

    val row = GeometryEncoders.forPoints().apply(point)
    row.getDouble(0) shouldBe point.x
    row.getDouble(1) shouldBe point.y
    assertSpatialReferenceEncodingForGeometry(point, row)
  }

  /**
   * Asserts that a polygon geometry is encoded with the expected rings and spatial reference.
   *
   * @param polygon polygon geometry to encode and verify
   * @since 0.1.0
   */
  private def assertPolygonEncoding(polygon: PolygonGeometry): Unit = {

    val row = GeometryEncoders.forPolygons().apply(polygon)
    val rings = row.getArray(0)

    // Assertions on rings (Array(Array(Array(Double))))
    rings.numElements() shouldBe polygon.numberOfPolygons
    rings.array(0) shouldBe a [ArrayData]

    // Assertions on first polygon (Array(Array(Double)))
    val firstSetOfPoints = rings.array(0).asInstanceOf[ArrayData]
    firstSetOfPoints.numElements() shouldBe polygon.numberOfVerticesInPolygon(0)

    // Assertions on first point (Array(Double)): should have size 2 since it's a point
    firstSetOfPoints.array(0) shouldBe a [ArrayData]
    val firstPoint = firstSetOfPoints.array(0).asInstanceOf[ArrayData]
    firstPoint.numElements() shouldBe 2

    assertSpatialReferenceEncodingForGeometry(polygon, row)
  }

  describe(`object`[GeometryEncoders.type ]) {
    describe(SHOULD) {
      describe("provide encoders for") {
        it("spatial reference") {

          // [1] Spatial reference with both WKID and latestWKID
          assertSpatialReferenceEncoding(
            SpatialReference(Some(1), Some(2))
          )

          // [2] Spatial reference with only wkid
          assertSpatialReferenceEncoding(
            SpatialReference(Some(3), None)
          )

          // [3] Spatial reference with only latestWKID
          assertSpatialReferenceEncoding(
            SpatialReference(None, Some(4))
          )
        }

        it("points") {

          // [1] Point without spatial reference
          assertPointEncoding(
            PointGeometry(1.23, 4.56, None)
          )

          // [2] Point with spatial reference
          assertPointEncoding(
            PointGeometry(1.23, 4.56, Some(
              SpatialReference(Some(4326), None))
            )
          )
        }

        it("polygons") {

          // [1] Polygon without spatial reference
          val polygonWithoutSr = PolygonGeometry(
            Seq(
              Seq(
                Seq(1.23, 4.56)
              )
            ), None
          )

          assertPolygonEncoding(polygonWithoutSr)

          // [2] Polygon with spatial reference
          val polygonWithSr = PolygonGeometry(
            Seq(
              Seq(
                Seq(1.23, 4.56)
              )
            ),
            Some(
              SpatialReference(
                Some(4326), None
              )
            )
          )
          assertPolygonEncoding(polygonWithSr)
        }
      }
    }
  }
}
