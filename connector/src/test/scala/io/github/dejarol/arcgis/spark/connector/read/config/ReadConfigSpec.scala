package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.config.{BaseConfig, NoSuchPropertyException}
import io.github.dejarol.arcgis.spark.connector.core.{BasicSpec, JavaMapMixins}
import org.scalatest.enablers.KeyMapping
import sttp.model.Uri

class ReadConfigSpec
  extends BasicSpec {

  import ReadConfigSpec._

  private lazy val emptyReadConfig: ReadConfig = createEmptyReadConfig()

  describe(anInstanceOf[ReadConfig]) {
    describe(SHOULD) {
      it("retrieve the layerUri") {

        a [NoSuchPropertyException] shouldBe thrownBy {
          emptyReadConfig.layerUri
        }

        val value = "http://localhost:6080/arcgis/rest/services/ServiceName/MapServer/0"
        val valid = createReadConfig(ReadConfig.LAYER_URI_KEY, value)
        valid.layerUri shouldBe Uri.unsafeParse(value)
      }

      it("retrieve query options") {

        emptyReadConfig.queryLayerConfig shouldBe empty

        val queryConfig = createReadConfig(
          (ReadConfig.QUERY_PREFIX + "k1", "v1"),
          ("k2", "v2")
        ).queryLayerConfig

        queryConfig shouldNot be (empty)
        queryConfig should contain key "k1"
        queryConfig shouldNot contain key "k2"
      }

      it("retrieve partitioning options") {

        emptyReadConfig.partitioningConfig shouldBe empty

        val partitioningConfig = createReadConfig(
          (ReadConfig.PARTITIONING_PREFIX + "k1", "v1"),
          ("k2", "v2")
        ).partitioningConfig

        partitioningConfig shouldNot be (empty)
        partitioningConfig should contain key "k1"
        partitioningConfig shouldNot contain key "k2"
      }
    }
  }
}

object ReadConfigSpec
  extends JavaMapMixins {

  /**
   * Enables ScalaTest `contain key` matchers on [[BaseConfig]].
   *
   * @since 0.1.0
   */
  lazy implicit val BASE_CONFIG_KEY_MAPPING: KeyMapping[BaseConfig] =
    (map: BaseConfig, key: Any) => map.containsKey(String.valueOf(key))

  /**
   * Creates an empty read configuration for the tests in this suite.
   *
   * @return a read configuration with no properties
   * @since 0.1.0
   */
  private def createEmptyReadConfig(): ReadConfig = {

    ReadConfig(
      createEmptyMap()
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
  private def createReadConfig(
                                k: String,
                                v: String
                              ): ReadConfig = {

    ReadConfig(
      createCIMap(k, v)
    )
  }

  /**
   * Creates a read configuration from one or more entries for the tests in this suite.
   *
   * @param first  first property entry
   * @param others additional property entries
   * @return a read configuration containing the given entries
   * @since 0.1.0
   */
  private def createReadConfig(
                                first: (String, String),
                                others: (String, String)*
                              ): ReadConfig = {

    ReadConfig(
      createSimpleMap(first, others: _*)
    )
  }
}
