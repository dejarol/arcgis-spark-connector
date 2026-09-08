package io.github.dejarol.arcgis.spark.connector.core.json

import io.github.dejarol.arcgis.spark.connector.core.models._
import io.github.dejarol.arcgis.spark.connector.core.{BasicSpec, EnumWithAPIName, JSONMixins}
import org.json4s.DefaultFormats

class CustomizationsSpec
  extends BasicSpec
    with JSONMixins {

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
      }
    }
  }
}
