package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.{BasicSpec, JavaMapMixins}

class PartitioningConfigSpec
  extends BasicSpec
    with JavaMapMixins {

  private lazy val emptyConfig = PartitioningConfig(createEmptyMap())

  describe(anInstanceOf[PartitioningConfig]) {
    describe(SHOULD) {
      it("retrieve the maximum number of records per query") {

        // [1.1] Empty config
        emptyConfig.maxRecordsPerQuery shouldBe PartitioningConfig.MAX_RECORDS_PER_QUERY_DEFAULT

        // [1.2] Config with a custom value
        PartitioningConfig(
          createSingletonMap(PartitioningConfig.MAX_RECORDS_PER_QUERY_KEY, "5")
        ).maxRecordsPerQuery shouldBe 5
      }
    }
  }
}
