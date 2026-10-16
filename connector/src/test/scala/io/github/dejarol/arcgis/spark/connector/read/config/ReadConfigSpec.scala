package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.ConfigSpec
import io.github.dejarol.arcgis.spark.connector.core.config.{BaseConfig, NoSuchPropertyException}
import io.github.dejarol.arcgis.spark.connector.core.http.{DefaultSyncBackendFactory, DisabledCertValidationBackendFactory}
import org.apache.spark.sql.util.CaseInsensitiveStringMap
import org.scalatest.OptionValues
import org.scalatest.enablers.KeyMapping
import sttp.model.Uri

import java.util

class ReadConfigSpec
  extends ConfigSpec
    with OptionValues {

  import ReadConfigSpec._

  private lazy val emptyReadConfig: ReadConfig = ReadConfig(EMPTY_CIMAP)

  describe(`object`[ReadConfig]) {
    describe(SHOULD) {
      describe("create an instance from") {
        it("a single CI map") {

          val ciMap: CaseInsensitiveStringMap = new CaseInsensitiveStringMap(
            new util.HashMap[String, String]() {{
              put("k1", "v1")
            }}
          )

          val config = ReadConfig.fromCIMap(ciMap)
          config should contain key "k1"
        }

        it("the union of CI maps") {

          val first: util.Map[String, String] = new util.HashMap[String, String]() {{
            put("k1", "v1")
            put("k2", "v2")
          }}

          val second: util.Map[String, String] = new util.HashMap[String, String]() {{
            put("k2", "v3")
          }}

          val config = ReadConfig.fromUnionOf(
            new CaseInsensitiveStringMap(first),
            new CaseInsensitiveStringMap(second)
          )

          config should contain key "k1"
          config should contain key "k2"
          config("k1") shouldBe "v1"
          config("k2") shouldBe "v3"
        }
      }
    }
  }

  describe(anInstanceOf[ReadConfig]) {
    describe(SHOULD) {
      describe("retrieve") {
        it("the layerUri") {

          // [1.1] Empty config
          a[NoSuchPropertyException] shouldBe thrownBy {
            emptyReadConfig.layerUri
          }

          // [1.2] Config with 'layerUri' option
          val value = "http://localhost:6080/arcgis/rest/services/ServiceName/MapServer/0"
          val configWithLayerUriOption = ReadConfig(
            createSingletonCIMap(ReadConfig.LAYER_URI_KEY, value)
          )
          configWithLayerUriOption.layerUri shouldBe Uri.unsafeParse(value)

          // [1.3] Config with 'path' option
          val configWithPathOption = ReadConfig(
            createSingletonCIMap("path", value)
          )

          configWithPathOption.layerUri shouldBe Uri.unsafeParse(value)
        }

        it("query options") {

          emptyReadConfig.queryLayerConfig shouldBe empty

          val queryConfig = ReadConfig(
            createCIMap(
              Map(
                ReadConfig.QUERY_PREFIX + "k1" -> "v1",
                "k2" -> "v2"
              )
            )
          ).queryLayerConfig

          queryConfig shouldNot be(empty)
          queryConfig should contain key "k1"
          queryConfig shouldNot contain key "k2"
        }

        it("partitioning options") {

          emptyReadConfig.partitioningConfig shouldBe empty

          val partitioningConfig = ReadConfig(
            createCIMap(
              Map(
                ReadConfig.PARTITIONING_PREFIX + "k1" -> "v1",
                "k2" -> "v2"
              )
            )
          ).partitioningConfig

          partitioningConfig shouldNot be(empty)
          partitioningConfig should contain key "k1"
          partitioningConfig shouldNot contain key "k2"
        }

        it("the backend factory") {

          // [1] Default: option is missing
          emptyReadConfig.backendFactory shouldBe a[DefaultSyncBackendFactory.type]

          // [2] options is set to false
          ReadConfig(
            createCIMap(
              Map(
                ReadConfig.DISABLE_CERT_VALIDATION -> "false"
              )
            )
          ).backendFactory shouldBe a[DefaultSyncBackendFactory.type]

          // [3] option is set to true
          ReadConfig(
            createCIMap(
              Map(
                ReadConfig.DISABLE_CERT_VALIDATION -> "true"
              )
            )
          ).backendFactory shouldBe a[DisabledCertValidationBackendFactory.type]
        }

        it("auth options") {

          emptyReadConfig.authConfig shouldBe empty

          val authConfig = ReadConfig(
            createCIMap(
              Map(
                ReadConfig.AUTH_PREFIX + "k1" -> "v1",
                "k2" -> "v2"
              )
            )
          ).authConfig

          authConfig shouldNot be(empty)
          authConfig should contain key "k1"
          authConfig shouldNot contain key "k2"
        }

        it("pushdownPredicate") {

          // [1] Empty config: should return default
          emptyReadConfig.pushdownPredicate shouldBe true

          // [2] Non-empty config: should retrieve the user-provided value
          ReadConfig(
            createSingletonCIMap(ReadConfig.PUSHDOWN_PREDICATE_KEY, "false")
          ).pushdownPredicate shouldBe false

          ReadConfig(
            createSingletonCIMap(ReadConfig.PUSHDOWN_PREDICATE_KEY, "true")
          ).pushdownPredicate shouldBe true
        }
      }

      describe("enrich the existing options with") {
        it("outFields") {

          val outFields = Seq("ID", "NAME")
          emptyReadConfig.queryLayerConfig.outFields shouldBe empty
          emptyReadConfig.withOutFields(
            outFields
          ).queryLayerConfig.outFields.value should contain theSameElementsAs outFields
        }

        it("returnGeometry") {

          val flag = true
          emptyReadConfig.queryLayerConfig.returnGeometry shouldBe empty
          emptyReadConfig.withReturnGeometry(
            flag
          ).queryLayerConfig.returnGeometry.value shouldBe flag
        }

        it("where") {

          // Empty config should have no where
          emptyReadConfig.queryLayerConfig.where shouldBe empty

          // Empty config enriched with an empty Seq should have no where
          emptyReadConfig.withWhere(Seq.empty).queryLayerConfig.where shouldBe empty

          // Empty config enriched with one condition should have that where
          val (first, second) = ("GEOID = '01'", "NAME = 'john'")
          emptyReadConfig.withWhere(Seq(first)).queryLayerConfig.where.value shouldBe first

          // Empty config enriched with 2 conditions should have the logical AND of the 2 conditions
          emptyReadConfig.withWhere(
            Seq(first, second)
          ).queryLayerConfig.where.value shouldBe s"($first) and ($second)"

          // Non-empty config enriched with 1 condition should have the logical AND of the 2 conditions
          ReadConfig(
            createSingletonCIMap(
              ReadConfig.QUERY_PREFIX + QueryLayerConfig.WHERE_KEY, first
            )
          ).withWhere(Seq(second)).queryLayerConfig.where.value shouldBe s"($first) and ($second)"
        }
      }
    }
  }
}

object ReadConfigSpec {

  /**
   * Enables ScalaTest `contain key` matchers on [[BaseConfig]].
   *
   * @since 0.1.0
   */
  lazy implicit val BASE_CONFIG_KEY_MAPPING: KeyMapping[BaseConfig] =
    (map: BaseConfig, key: Any) => map.contains(String.valueOf(key))
}
