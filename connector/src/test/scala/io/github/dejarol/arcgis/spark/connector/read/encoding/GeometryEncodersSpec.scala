package io.github.dejarol.arcgis.spark.connector.read.encoding

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec
import io.github.dejarol.arcgis.spark.connector.core.models.SpatialReference
import io.github.dejarol.arcgis.spark.connector.core.schema.SparkGeometryTypes
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.util.ArrayData
import org.json4s.native.JsonMethods

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
   * Asserts that a spatial reference is encoded with the expected WKID fields.
   *
   * @param actual the internal row
   * @param expected the expected spatial reference
   * @since 0.1.0
   */
  private def assertSpatialReferenceEncoding(
                                              actual: InternalRow,
                                              expected: SpatialReference
                                            ): Unit = {

    // [1] Assert the encoding for wkid
    expected.wkid match {
      case Some(value) => actual.getInt(0) shouldBe value
      case None => actual.isNullAt(0) shouldBe true
    }

    // [2] Assert the encoding for latestWkid
    expected.latestWkid match {
      case Some(value) => actual.getInt(1) shouldBe value
      case None => actual.isNullAt(1) shouldBe true
    }
  }

  /**
   * Asserts that a point geometry is encoded with the expected coordinates and spatial reference.
   *
   * @param json point geometry to encode and verify
   * @since 0.1.0
   */
  private def assertPointEncoding(
                                   json: String,
                                   expectedX: Double,
                                   expectedY: Double,
                                   spatialReference: Option[SpatialReference]
                                 ): Unit = {

    // [1] Assert that the geometry is encoded correctly
    val row = GeometryEncoders.forPoints().apply(
      JsonMethods.parse(json)
    )

    // [2] Assert that the coordinates are encoded correctly
    row.getDouble(0) shouldBe expectedX
    row.getDouble(1) shouldBe expectedY

    // [3] Assert that the spatial reference is encoded correctly
    spatialReference match {
      case Some(value) =>

        // [3.1] Assert that the spatial reference is encoded correctly
        val sr = row.get(2, SparkGeometryTypes.POINT)
        sr shouldBe a [InternalRow]
        assertSpatialReferenceEncoding(sr.asInstanceOf[InternalRow], value)

      case None =>

        // [3.2] Assert that the spatial reference is null
        row.isNullAt(2) shouldBe true
    }
  }

  /**
   * Asserts that a polygon geometry is encoded with the expected rings and spatial reference.
   *
   * @param json polygon geometry to encode and verify
   * @since 0.1.0
   */
  //noinspection SameParameterValue
  private def assertPolygonEncoding(
                                     json: String,
                                     expectedNumberOfPolygons: Int,
                                     spatialReference: Option[SpatialReference]
                                   ): Unit = {

    // [1] Assert that the geometry is encoded correctly
    val row = GeometryEncoders.forPolygons().apply(
      JsonMethods.parse(json)
    )

    // [2] Assertions on rings (Array(Array(Array(Double))))
    val rings = row.getArray(0)
    rings.numElements() shouldBe expectedNumberOfPolygons
    rings.array(0) shouldBe a [ArrayData]

    // [3] Assertions on first point (Array(Double)): should have size 2 since it's a point
    val firstSetOfPoints = rings.array(0).asInstanceOf[ArrayData]
    firstSetOfPoints.array(0) shouldBe a [ArrayData]
    val firstPoint = firstSetOfPoints.array(0).asInstanceOf[ArrayData]
    firstPoint.numElements() shouldBe 2

    // [4] Assertions on spatial reference
    spatialReference match {
      case Some(value) =>

        // [4.1] Assert that the spatial reference is encoded correctly
        val sr = row.get(1, SparkGeometryTypes.SPATIAL_REFERENCE_TYPE)
        sr shouldBe a [InternalRow]
        assertSpatialReferenceEncoding(sr.asInstanceOf[InternalRow], value)

      case None =>

        // [4.2] Assert that the spatial reference is null
        row.isNullAt(1) shouldBe true
    }
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

          val (x, y, wkid) = (1.23, 4.56, 4326)
          val pointWithoutSR =
            f"""
               |{
               |  "x" : $x,
               |  "y" : $y
               |}""".stripMargin

          // [1] Point without spatial reference
          assertPointEncoding(
            pointWithoutSR, x, y, None
          )

          val pointWithSR =
            f"""
               |{
               |  "x": $x,
               |  "y": $y,
               |  "spatialReference": {
               |    "wkid": $wkid
               |  }
               |}""".stripMargin

          // [2] Point with spatial reference
          assertPointEncoding(
            pointWithSR, x, y, Some(SpatialReference(Some(wkid), None))
          )
        }

        it("polygons") {

          // [1] Polygon without spatial reference
          val polygonWithoutSr =
            f"""
               |{
               |  "rings": [
               |    [
               |      [1.23, 4.56]
               |    ]
               |  ]
               |}""".stripMargin

          assertPolygonEncoding(
            polygonWithoutSr, 1, None
          )

          // [2] Polygon with spatial reference
          val (wkid, latestWkid) = (1, 2)
          val polygonWithSr =
            f"""
               |{
               |  "rings": [
               |    [
               |      [1.23, 4.56]
               |    ]
               |  ],
               |  "spatialReference": {
               |    "wkid": $wkid,
               |    "latestWkid": $latestWkid
               |  }
               |}""".stripMargin

          assertPolygonEncoding(
            polygonWithSr, 1 , Some(
              SpatialReference(Some(wkid), Some(latestWkid))
            )
          )
        }
      }
    }
  }
}
