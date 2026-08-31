package io.github.dejarol.arcgis.spark.connector.core.schema

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec
import io.github.dejarol.arcgis.spark.connector.core.models.{EsriFieldType, EsriGeometryType, FeatureLayerField}
import org.apache.spark.sql.types.{DataType, DoubleType, IntegerType, StringType, StructType, TimestampType}
import org.scalatest.Inspectors

class SchemaUtilsSpec
  extends BasicSpec
    with Inspectors {

  import SchemaUtilsSpec._

  /**
   * Asserts that an ArcGIS field type maps to the expected Spark data type.
   *
   * @param name              Spark field name
   * @param arcgisType        ArcGIS field type to convert
   * @param expectedSparkType Spark data type expected after conversion
   * @since 0.1.0
   */
  //noinspection SameParameterValue
  private def assertArcgisSparkTypeMapping(
                                            name: String,
                                            arcgisType: EsriFieldType,
                                            expectedSparkType: DataType
                                          ): Unit = {

    val input = createFeatureLayerField(name, arcgisType)
    val actual = SchemaUtils.featureLayerFieldToStructField(input)
    actual.name shouldBe name
    actual.dataType shouldBe expectedSparkType
  }

  /**
   * Asserts that an ArcGIS geometry type maps to the expected Spark geometry schema.
   *
   * @param esriGeometryType ArcGIS geometry type to convert
   * @param expected         Spark schema expected for the geometry column
   * @since 0.1.0
   */
  private def assertEsriGeometryColumnMapping(
                                               esriGeometryType: EsriGeometryType,
                                               expected: StructType
                                             ): Unit = {

    val actual = SchemaUtils.geometryField(esriGeometryType)
    actual.name shouldBe SchemaUtils.GEOMETRY_COLUMN_NAME
    actual.dataType shouldBe expected
  }

  describe(`object`[SchemaUtils.type ]) {
    describe(SHOULD) {
      it("map a feature layer field to a struct field") {

        assertArcgisSparkTypeMapping("name", EsriFieldType.STRING, StringType)
        assertArcgisSparkTypeMapping("name", EsriFieldType.DOUBLE, DoubleType)
        assertArcgisSparkTypeMapping("name", EsriFieldType.INTEGER, IntegerType)
        assertArcgisSparkTypeMapping("name", EsriFieldType.OID, IntegerType)
        assertArcgisSparkTypeMapping("name", EsriFieldType.DATE, TimestampType)
        assertArcgisSparkTypeMapping("name", EsriFieldType.SMALL_INTEGER, IntegerType)
      }

      it("define the geometry column") {

        assertEsriGeometryColumnMapping(EsriGeometryType.POINT, SparkGeometryTypes.POINT)
        assertEsriGeometryColumnMapping(EsriGeometryType.POLYGON, SparkGeometryTypes.POLYGON)
      }

      describe("convert a collection of struct fields to a struct type") {
        it("adding the geometry column") {

          val input = Seq(
            createFeatureLayerField("objectID", EsriFieldType.OID),
            createFeatureLayerField("name", EsriFieldType.STRING),
            createFeatureLayerField("value", EsriFieldType.DOUBLE)
          )

          val actual = SchemaUtils.toStructType(input, Some(EsriGeometryType.POINT))
          actual should have size (input.size + 1)
          actual.fieldNames should contain theSameElementsInOrderAs (
            input.map(_.name) :+ SchemaUtils.GEOMETRY_COLUMN_NAME
            )

          val expectedDataTypes = Map(
            "objectID" -> IntegerType,
            "name" -> StringType,
            "value" -> DoubleType,
            SchemaUtils.GEOMETRY_COLUMN_NAME -> SparkGeometryTypes.POINT
          )

          forAll(actual) {
            field =>
              expectedDataTypes should contain key field.name
              field.dataType shouldBe expectedDataTypes(field.name)
          }
        }

        it("without adding the geometry column") {

          val input = Seq(
            createFeatureLayerField("objectID", EsriFieldType.OID),
            createFeatureLayerField("name", EsriFieldType.STRING)
          )

          val actual = SchemaUtils.toStructType(input, None)
          actual should have size input.size
          actual.fieldNames should contain theSameElementsInOrderAs input.map(_.name)

          val expectedDataTypes = Map(
            "objectID" -> IntegerType,
            "name" -> StringType
          )

          forAll(actual) {
            field =>
              expectedDataTypes should contain key field.name
              field.dataType shouldBe expectedDataTypes(field.name)
          }
        }
      }
    }
  }
}

object SchemaUtilsSpec {

  /**
   * Creates a feature layer field with the given name and ArcGIS type.
   *
   * @param name   field name
   * @param `type` ArcGIS field data type
   * @return a feature layer field for the tests in this suite
   * @since 0.1.0
   */
  private def createFeatureLayerField(
                                       name: String,
                                       `type`: EsriFieldType
                                     ): FeatureLayerField = {

    FeatureLayerField(name, `type`)
  }
}
