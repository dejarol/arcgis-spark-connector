package io.github.dejarol.arcgis.spark.connector.read.http

import io.github.dejarol.arcgis.spark.connector.core.schema.SchemaUtils
import io.github.dejarol.arcgis.spark.connector.read.config.{QueryLayerConfig, ReadConfig}
import io.github.dejarol.arcgis.spark.connector.{ArcgisIntegrationSpec, ArcgisTableProvider, SparkSpec}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types.StructType

class ReadIntegrationSpec
  extends ArcgisIntegrationSpec
    with SparkSpec {

  /**
   * TODO
   * @param options
   * @return
   */
  private def invokeDatasourceWithOptions(options: Map[String, String]): DataFrame = {

    spark.read.format(ArcgisTableProvider.SHORT_NAME)
      .options(options)
      .load("")
  }

  describe(s"Datasource ${ArcgisTableProvider.SHORT_NAME}") {
    describe(SHOULD) {
      describe("let users query a feature layer") {
        it("setting a where condition") {

          val df = invokeDatasourceWithOptions(
            Map(
              ReadConfig.LAYER_URI_KEY -> polygonLayerUri.toString(),
              ReadConfig.QUERY_PREFIX + QueryLayerConfig.WHERE_KEY -> "GEOID = '01'"
            )
          )

          df.count() shouldBe 1
        }

        it("setting some objectIDs") {

          val df = invokeDatasourceWithOptions(
            Map(
              ReadConfig.LAYER_URI_KEY -> polygonLayerUri.toString(),
              ReadConfig.QUERY_PREFIX + QueryLayerConfig.OBJECT_IDS_KEY -> "1"
            )
          )

          df.count() shouldBe 1
        }

        it("setting some outFields") {

          val fields = Seq("GEOID", "NAME")
          val df = invokeDatasourceWithOptions(
            Map(
              ReadConfig.LAYER_URI_KEY -> polygonLayerUri.toString(),
              ReadConfig.QUERY_PREFIX + QueryLayerConfig.OUT_FIELDS_KEY -> fields.mkString(",")
            )
          )

          df.columns should contain theSameElementsAs fields
        }

        describe("returning the geometry") {
          it("for point layers") {

            val fields = Seq("OBJECTID")
            val df = invokeDatasourceWithOptions(
              Map(
                ReadConfig.LAYER_URI_KEY -> pointLayerUri.toString(),
                ReadConfig.QUERY_PREFIX + QueryLayerConfig.OUT_FIELDS_KEY -> fields.mkString(","),
                ReadConfig.QUERY_PREFIX + QueryLayerConfig.RETURN_GEOMETRY_KEY -> "true"
              )
            )

            df.columns should contain theSameElementsAs (fields :+ SchemaUtils.GEOMETRY_COLUMN_NAME)
            val geometryField = df.schema.apply(SchemaUtils.GEOMETRY_COLUMN_NAME)
            geometryField.dataType shouldBe a[StructType]
            geometryField.dataType
              .asInstanceOf[StructType]
              .fieldNames should contain theSameElementsAs Seq("x", "y", "spatialReference")
          }

          it("for polygon layers") {

            val fields = Seq("GEOID", "NAME")
            val df = invokeDatasourceWithOptions(
              Map(
                ReadConfig.LAYER_URI_KEY -> polygonLayerUri.toString(),
                ReadConfig.QUERY_PREFIX + QueryLayerConfig.OUT_FIELDS_KEY -> fields.mkString(","),
                ReadConfig.QUERY_PREFIX + QueryLayerConfig.RETURN_GEOMETRY_KEY -> "true"
              )
            )

            df.columns should contain theSameElementsAs (fields :+ SchemaUtils.GEOMETRY_COLUMN_NAME)
            val geometryField = df.schema.apply(SchemaUtils.GEOMETRY_COLUMN_NAME)
            geometryField.dataType shouldBe a[StructType]
            geometryField.dataType
              .asInstanceOf[StructType]
              .fieldNames should contain theSameElementsAs Seq("rings", "spatialReference")
          }
        }
      }
    }
  }
}