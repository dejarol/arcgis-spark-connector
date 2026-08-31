package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.ConfigSpec
import org.scalatest.OptionValues

class PartitioningConfigSpec
  extends ConfigSpec
    with OptionValues {

  private lazy val emptyConfig = PartitioningConfig(EMPTY_CIMAP)

  describe(anInstanceOf[PartitioningConfig]) {
    describe(SHOULD) {
      describe("retrieve options for") {
        it("fetchSize") {

          // [1.1] Empty config
          emptyConfig.fetchSize shouldBe PartitioningConfig.FETCH_SIZE_DEFAULT

          // [1.2] Config with a custom value
          PartitioningConfig(
            createCIMap(
              Map(PartitioningConfig.FETCH_SIZE_KEY -> "5")
            )
          ).fetchSize shouldBe 5
        }

        it("numPartitions") {

          emptyConfig.numPartitions shouldBe empty

          PartitioningConfig(
            createCIMap(
              Map(PartitioningConfig.NUM_PARTITIONS_KEY -> "5")
            )
          ).numPartitions.value shouldBe 5
        }
      }
    }
  }
}
