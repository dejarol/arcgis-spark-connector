package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec
import io.github.dejarol.arcgis.spark.connector.core.config.{BaseConfig, NoSuchPropertyException}
import org.apache.spark.sql.catalyst.util.CaseInsensitiveMap
import org.apache.spark.sql.util.CaseInsensitiveStringMap
import org.scalatest.enablers.KeyMapping
import sttp.model.Uri

import java.util

class ReadConfigSpec
  extends BasicSpec {

  import ReadConfigSpec._

  private lazy val emptyReadConfig: ReadConfig = createEmptyConfig()

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

        a [NoSuchPropertyException] shouldBe thrownBy {
          emptyReadConfig.layerUri
        }

        val value = "http://localhost:6080/arcgis/rest/services/ServiceName/MapServer/0"
        val valid = createSingletonConfig(ReadConfig.LAYER_URI_KEY, value)
        valid.layerUri shouldBe Uri.unsafeParse(value)
      }

      it("retrieve query options") {

        emptyReadConfig.queryLayerConfig shouldBe empty

        val queryConfig = createReadConfig(
          Map(
            ReadConfig.QUERY_PREFIX + "k1" -> "v1",
            "k2" -> "v2"
          )
        ).queryLayerConfig

        queryConfig shouldNot be (empty)
        queryConfig should contain key "k1"
        queryConfig shouldNot contain key "k2"
      }

      it("retrieve partitioning options") {

        emptyReadConfig.partitioningConfig shouldBe empty

        val partitioningConfig = createReadConfig(
          Map(
            ReadConfig.PARTITIONING_PREFIX + "k1" -> "v1",
            "k2" -> "v2"
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

  /**
   * TODO
   * @return
   */
  private def createEmptyConfig(): ReadConfig = {

    ReadConfig(
      CaseInsensitiveMap(
        Map.empty
      )
    )
  }

  /**
   * Creates a read configuration with a single property for the tests in this suite.
   *
   * @param k property name
   * @param v property value
   * @return a read configuration containing only that entry
   * @since 0.1.0
   */
  private def createSingletonConfig(
                                     k: String,
                                     v: String
                                   ): ReadConfig = {

    ReadConfig(
      CaseInsensitiveMap(
        Map(k -> v)
      )
    )
  }

  /**
   * Creates a read configuration from one or more entries for the tests in this suite.
   *
   * @param map: map of property entries
   * @return a read configuration containing the given entries
   * @since 0.1.0
   */
  private def createReadConfig(map: Map[String, String]): ReadConfig = {

    ReadConfig(
      CaseInsensitiveMap(map)
    )
  }
}
