package io.github.dejarol.arcgis.spark.connector.read.http

import io.github.dejarol.arcgis.spark.connector.core.schema.SchemaUtils
import io.github.dejarol.arcgis.spark.connector.read.ArcgisScan
import io.github.dejarol.arcgis.spark.connector.read.config.{QueryLayerConfig, ReadConfig}
import io.github.dejarol.arcgis.spark.connector.{ArcgisIntegrationSpec, ArcgisTableProvider, SparkMixins}
import org.apache.spark.sql.execution.datasources.v2.BatchScanExec
import org.apache.spark.sql.{DataFrame, functions => f}
import org.apache.spark.sql.types.StructType

class ReadIntegrationSpec
  extends ArcgisIntegrationSpec
    with SparkMixins {

  /**
   * Invokes the datasource defined in this project in read mode using the given options.
   *
   * @param options read options
   * @return a DataFrame containing the query results
   * @since 0.2.0
   */
  private def invokeDatasourceWithOptions(options: Map[String, String]): DataFrame = {

    spark.read.format(ArcgisTableProvider.SHORT_NAME)
      .options(options)
      .load()
  }

  /**
   * Finds the [[ArcgisScan]] used by the given DataFrame's executed plan, if present.
   *
   * @param df DataFrame produced by this datasource
   * @return the scan implementation, or `None` if the plan has no [[ArcgisScan]]
   * @since 0.2.0
   */
  private def scanImplementationOf(df: DataFrame): Option[ArcgisScan] = {

    df.queryExecution.executedPlan
      .collectFirst {
        case scan: BatchScanExec =>
          scan.scan
      }.collect {
        case scan: ArcgisScan => scan
      }
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

      describe("support") {
        describe("pushing required columns") {
          it("when returnGeometry is not set") {

            val requiredColumns = Seq("GEOID", "NAME")
            val df = invokeDatasourceWithOptions(
              Map(
                ReadConfig.LAYER_URI_KEY -> polygonLayerUri.toString()
              )
            ).select(requiredColumns.map(f.col): _*)

            // Inspect settings on the scan. We expect
            // [a] the scan schema to have only the required columns
            // [b] the scan outFIelds to have the same required columns
            val maybeScan = scanImplementationOf(df)
            maybeScan shouldBe defined
            val scan = maybeScan.get

            scan.readSchema() should have size requiredColumns.size
            scan.readSchema().fieldNames should contain theSameElementsAs requiredColumns

            val outFields = scan.config.queryLayerConfig.outFields
            outFields shouldBe defined
            outFields.get should have size requiredColumns.size
            outFields.get should contain theSameElementsAs requiredColumns

            df.columns should have size requiredColumns.size
            df.columns should contain theSameElementsAs requiredColumns
          }

          it("when returnGeometry is set, but geometryColumn is not requested") {

            val requiredColumns = Seq("GEOID", "NAME")
            val df = invokeDatasourceWithOptions(
              Map(
                ReadConfig.LAYER_URI_KEY -> polygonLayerUri.toString(),
                ReadConfig.QUERY_PREFIX + QueryLayerConfig.RETURN_GEOMETRY_KEY -> "true"
              )
            ).select(requiredColumns.map(f.col): _*)

            // Inspect settings on the scan. We expect
            // [a] the scan schema to have only the required columns
            // [b] the scan outFIelds to have the same required columns
            // [c] the scan returnGeometry flag set to false

            val maybeScan = scanImplementationOf(df)
            maybeScan shouldBe defined
            val scan = maybeScan.get

            scan.readSchema() should have size requiredColumns.size
            scan.readSchema().fieldNames should contain theSameElementsAs requiredColumns

            val scanQueryLayerConfig = scan.config.queryLayerConfig
            scanQueryLayerConfig.returnGeometry.get shouldBe false
            val outFields = scanQueryLayerConfig.outFields
            outFields shouldBe defined
            outFields.get should have size requiredColumns.size
            outFields.get should contain theSameElementsAs requiredColumns

            df.columns should have size requiredColumns.size
            df.columns should contain theSameElementsAs requiredColumns
          }

          it("when returnGeometry is set, and geometryColumn is requested") {

            val requiredColumns = Seq("GEOID", "NAME", SchemaUtils.GEOMETRY_COLUMN_NAME)
            val df = invokeDatasourceWithOptions(
              Map(
                ReadConfig.LAYER_URI_KEY -> polygonLayerUri.toString(),
                ReadConfig.QUERY_PREFIX + QueryLayerConfig.RETURN_GEOMETRY_KEY -> "true"
              )
            ).select(requiredColumns.map(f.col): _*)

            // Inspect settings on the scan. We expect
            // [a] the scan schema to have only the required columns
            // [b] the scan outFields to have only the attribute columns
            // [c] the scan returnGeometry flag set to true

            val maybeScan = scanImplementationOf(df)
            maybeScan shouldBe defined
            val scan = maybeScan.get

            scan.readSchema() should have size requiredColumns.size
            scan.readSchema().fieldNames should contain theSameElementsAs requiredColumns

            val scanQueryLayerConfig = scan.config.queryLayerConfig
            scanQueryLayerConfig.returnGeometry.get shouldBe true
            val outFields = scanQueryLayerConfig.outFields
            outFields shouldBe defined
            outFields.get should have size requiredColumns.size - 1
            outFields.get should contain theSameElementsAs requiredColumns.filterNot {
              _.equalsIgnoreCase(SchemaUtils.GEOMETRY_COLUMN_NAME)
            }

            df.columns should have size requiredColumns.size
            df.columns should contain theSameElementsAs requiredColumns
          }
        }

        describe("pushing some predicates, like") {
          it("a") {

            val df = invokeDatasourceWithOptions(
              Map(
                ReadConfig.LAYER_URI_KEY -> polygonLayerUri.toString()
              )
            ).filter(
              f.col("GEOID") === "01"
            ).collect()

            val a = 1
          }
        }
      }
    }
  }
}