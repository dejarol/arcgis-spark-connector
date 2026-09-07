package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.ConfigSpec
import io.github.dejarol.arcgis.spark.connector.core.config.{BaseConfig, NoSuchPropertyException}
import org.apache.spark.sql.util.CaseInsensitiveStringMap
import org.scalatest.enablers.KeyMapping
import sttp.model.Uri

import java.util

class ReadConfigSpec
  extends ConfigSpec {

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

        it("the union of two case insensitive maps") {

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
      it("retrieve the layerUri") {

        // [1.1] Empty config
        a [NoSuchPropertyException] shouldBe thrownBy {
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

      it("retrieve query options") {

        emptyReadConfig.queryLayerConfig shouldBe empty

        val queryConfig = ReadConfig(
          createCIMap(
            Map(
              ReadConfig.QUERY_PREFIX + "k1" -> "v1",
              "k2" -> "v2"
            )
          )
        ).queryLayerConfig

        queryConfig shouldNot be (empty)
        queryConfig should contain key "k1"
        queryConfig shouldNot contain key "k2"
      }

      it("retrieve partitioning options") {

        emptyReadConfig.partitioningConfig shouldBe empty

        val partitioningConfig = ReadConfig(
          createCIMap(
            Map(
              ReadConfig.PARTITIONING_PREFIX + "k1" -> "v1",
              "k2" -> "v2"
            )
          )
        ).partitioningConfig

        partitioningConfig shouldNot be (empty)
        partitioningConfig should contain key "k1"
        partitioningConfig shouldNot contain key "k2"
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
