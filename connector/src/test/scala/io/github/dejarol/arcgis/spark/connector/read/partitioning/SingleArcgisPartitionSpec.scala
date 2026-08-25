package io.github.dejarol.arcgis.spark.connector.read.partitioning

import io.github.dejarol.arcgis.spark.connector.core.{BasicSpec, JavaMapMixins}
import io.github.dejarol.arcgis.spark.connector.read.config.QueryLayerConfig
import org.scalatest.{Inspectors, OptionValues}

class SingleArcgisPartitionSpec
  extends BasicSpec
    with Inspectors
      with OptionValues
        with JavaMapMixins {

  describe(anInstanceOf[SingleArcgisPartition]) {
    describe(SHOULD) {
      it("generate a sequence of queries") {

        val generatedQueries = SingleArcgisPartition(
          12, 5, QueryLayerConfig(
            createSingletonMap(
              QueryLayerConfig.WHERE_KEY, "name = 'john'"
            )
          )
        ).parametersForPartitionQueries

        generatedQueries should have size 3
        val expectation = Seq((0, 5), (5, 5), (10, 2))
        forAll(generatedQueries.zip(expectation)) {
          case (query, (offset, recordCount)) =>
            query.resultOffset.value shouldBe offset
            query.resultRecordCount.value shouldBe recordCount
        }
      }
    }
  }
}
