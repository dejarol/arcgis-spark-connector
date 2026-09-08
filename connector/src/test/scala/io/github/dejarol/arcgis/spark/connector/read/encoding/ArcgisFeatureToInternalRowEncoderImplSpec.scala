package io.github.dejarol.arcgis.spark.connector.read.encoding

import io.github.dejarol.arcgis.spark.connector.core.models.{EsriFieldType, EsriGeometryType, FeatureLayerField, Geometry, PointGeometry, PolygonGeometry}
import io.github.dejarol.arcgis.spark.connector.core.schema.SparkGeometryTypes
import io.github.dejarol.arcgis.spark.connector.core.{BasicSpec, JSONMixins}
import io.github.dejarol.arcgis.spark.connector.read.models.ArcgisFeature
import org.apache.spark.sql.catalyst.InternalRow
import org.json4s.JsonAST.JObject
import org.json4s.JsonDSL._

class ArcgisFeatureToInternalRowEncoderImplSpec
  extends BasicSpec
    with JSONMixins {

  private lazy val idField = FeatureLayerField("id", EsriFieldType.INTEGER)
  private lazy val nameField = FeatureLayerField("name", EsriFieldType.STRING)
  private lazy val (id, name) = (1, "john")

  /**
   * Creates an ArcGIS feature from a JSON object and an optional geometry.
   *
   * @param attributes the feature attributes
   * @param geometry the optional feature geometry
   * @tparam T the geometry type
   * @return an ArcGIS feature
   */
  private def createFeature[T <: Geometry with Product](
                                                         attributes: JObject,
                                                         geometry: Option[T]
                                                       ): ArcgisFeature = {

    ArcgisFeature(
      attributes.obj.toMap,
      geometry.map(caseClassToJObject)
    )
  }

  describe(anInstanceOf[ArcgisFeatureToInternalRowEncoderImpl]) {
    describe(SHOULD) {
      describe("raise an exception when") {
        it("a field does not exist") {

          val fields = Seq(idField, nameField)
          val jsonObject: JObject = "id" -> id
          val encoder = new ArcgisFeatureToInternalRowEncoderImpl(
            fields, None
          )

          a [NoSuchElementException] should be thrownBy {
            encoder.apply(
              createFeature[PointGeometry](jsonObject, None)
            )
          }
        }

        it("the geometry extraction is required, but a feature does not have a geometry") {

          val fields = Seq(idField)
          val jsonObject: JObject = "id" -> id
          val encoder = new ArcgisFeatureToInternalRowEncoderImpl(
            fields, Some(EsriGeometryType.POINT)
          )

          a [IllegalStateException] should be thrownBy {
            encoder.apply(
              createFeature[PointGeometry](jsonObject, None)
            )
          }
        }
      }

      describe("create an InternalRow from an ARCGIS feature") {
        it("having same number of fields as the layer") {

          val fields = Seq(idField, nameField)
          val jsonObject = ("id" -> id) ~~ ("name" -> name)
          val row = new ArcgisFeatureToInternalRowEncoderImpl(
            fields, None
          ).apply(
            createFeature[PointGeometry](
              jsonObject, None
            )
          )

          row.numFields shouldBe 2
          row.getInt(0) shouldEqual id
          row.getString(1) shouldEqual name
        }

        it("managing null values") {

          val fields = Seq(idField, nameField)
          val jsonObject = ("id" -> id) ~~ ("name" -> null.asInstanceOf[String])
          val row = new ArcgisFeatureToInternalRowEncoderImpl(
            fields, None
          ).apply(
            createFeature[PointGeometry](jsonObject, None)
          )

          row.numFields shouldBe 2
          row.getInt(0) shouldEqual id
          row.isNullAt(1) shouldEqual true
        }

        describe("extracting the geometry") {
          it("for points") {

            val fields = Seq(idField, nameField)
            val jsonObject = ("id" -> id) ~~ ("name" -> name)
            val row = new ArcgisFeatureToInternalRowEncoderImpl(
              fields, Some(EsriGeometryType.POINT)
            ).apply(
              createFeature[PointGeometry](
                jsonObject,
                Some(PointGeometry(0, 0, None))
              )
            )

            row.numFields shouldBe 3
            row.getInt(0) shouldEqual id
            row.getString(1) shouldEqual name
            row.get(2, SparkGeometryTypes.POINT) shouldBe a[InternalRow]
          }

          it("for polygons") {

            val fields = Seq(idField, nameField)
            val jsonObject = ("id" -> id) ~~ ("name" -> name)
            val row = new ArcgisFeatureToInternalRowEncoderImpl(
              fields, Some(EsriGeometryType.POLYGON)
            ).apply(
              createFeature[PolygonGeometry](
                jsonObject,
                Some(
                  PolygonGeometry(
                    Seq(
                      Seq(
                        Seq(1.2, 3.4)
                      )
                    ), None
                  )
                )
              )
            )

            row.numFields shouldBe 3
            row.getInt(0) shouldEqual id
            row.getString(1) shouldEqual name
            row.get(2, SparkGeometryTypes.POLYGON) shouldBe a[InternalRow]
          }
        }
      }
    }
  }
}
