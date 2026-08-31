package io.github.dejarol.arcgis.spark.connector.core.json

import io.github.dejarol.arcgis.spark.connector.core.models._
import io.github.dejarol.arcgis.spark.connector.core.{BasicSpec, EnumWithAPIName}
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Formats}

class CustomizationsSpec
  extends BasicSpec {

  /**
   * Parses a JSON string into a value of type `T`.
   *
   * @param rawJson JSON document to parse
   * @param formats JSON4s formats used for extraction
   * @tparam T expected value type
   * @return the extracted value
   * @since 0.1.0
   */
  private def jsonStringAS[T: Manifest](rawJson: String, formats: Formats): T = {

    JsonMethods.parse(rawJson)
      .extract[T](formats, manifest[T])
  }

  /**
   * Asserts that an enum with an ArcGIS API name can be deserialized from JSON.
   *
   * @param v enum constant expected after deserialization
   * @tparam E enum type exposing an ArcGIS API name
   * @since 0.1.0
   */
  private def assertDeserializationOfEnumWithAPIName[E <: Enum[E] with EnumWithAPIName: Manifest](v: E): Unit = {

    // [1.1] Produce a JSON string with one field ('type')
    // having as value the enum's API name
    val json =
      s"""
        |{
        | "type": "${v.getAPIName}"
        |}""".stripMargin

    // [1.2] Deserialize the JSON string into a model
    val model = jsonStringAS[ModelWithEnumWithAPIName[E]](
      json, DefaultFormats + Customizations.serializerForEnumWithAPIName[E]()
    )

    // [1.3] Assert that the deserialized model has the expected enum value
    model.`type` shouldEqual v
  }

  /**
   * Asserts that a JSON document deserializes to the expected geometry.
   *
   * @param rawJson  JSON document to parse
   * @param expected geometry expected after deserialization
   * @since 0.1.0
   */
  private def assertDeserializationOfGeometry(rawJson: String, expected: Geometry): Unit = {

    jsonStringAS[Geometry](
      rawJson, DefaultFormats + Customizations.serializerForGeometry()
    ) shouldEqual expected
  }

  describe(`object`[Customizations.type ]) {
    describe(SHOULD) {
      describe("provide JSON4S customizations for managing") {
        it("ARCGIS-related enums") {

          // Geometry types
          assertDeserializationOfEnumWithAPIName[EsriGeometryType](EsriGeometryType.POINT)
          assertDeserializationOfEnumWithAPIName[EsriGeometryType](EsriGeometryType.POLYGON)

          // Field types
          assertDeserializationOfEnumWithAPIName[EsriFieldType](EsriFieldType.OID)
          assertDeserializationOfEnumWithAPIName[EsriFieldType](EsriFieldType.STRING)
        }

        describe("the deserialization of geometries like") {
          it("points") {

            // [1.1] Produce a JSON string with numeric 'x' and 'y' fields plus 'spatialReference'
            val (x, y, wkid) = (1.23, 4.56, 4326)
            val jsonWithSR =
              f"""
                 |{
                 | "x": $x,
                 | "y": $y,
                 | "spatialReference": {
                 |   "wkid": $wkid
                 | }
                 |}""".stripMargin

            assertDeserializationOfGeometry(
              jsonWithSR, PointGeometry(
                x, y, Some(SpatialReference(Some(wkid), None))
              )
            )

            // [1.2] Produce a JSON string without 'spatialReference'
            val jsonWithoutSR =
              f"""
                 |{
                 |  "x": $x,
                 |  "y": $y
                 |}""".stripMargin

            assertDeserializationOfGeometry(
              jsonWithoutSR, PointGeometry(x, y, None)
            )
          }

          it("polygons") {

            // [1.1] Produce a JSON string with numeric 'rings' and 'spatialReference'
            val (x, y, wkid) = (1.23, 4.56, 4326)
            val jsonWithSR =
              f"""
                 |{
                 |  "rings": [
                 |    [
                 |      [$x, $y]
                 |    ]
                 |   ],
                 |   "spatialReference": {
                 |     "wkid": $wkid
                 |   }
                 |}
                 |""".stripMargin

            assertDeserializationOfGeometry(
              jsonWithSR, PolygonGeometry(
                Seq(Seq(Seq(x, y))),
                Some(SpatialReference(Some(wkid), None))
              )
            )

            // [1.2] Produce a JSON string without 'spatialReference'
            val jsonWithoutSR =
              f"""
                 |{
                 |  "rings": [
                 |    [
                 |      [$x, $y]
                 |    ]
                 |   ]
                 |}""".stripMargin

            assertDeserializationOfGeometry(
              jsonWithoutSR, PolygonGeometry(
                Seq(Seq(Seq(x, y))),
                None
              )
            )
          }
        }
      }
    }
  }
}
