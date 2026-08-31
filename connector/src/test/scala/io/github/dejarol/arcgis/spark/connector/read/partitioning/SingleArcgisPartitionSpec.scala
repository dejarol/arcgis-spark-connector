package io.github.dejarol.arcgis.spark.connector.read.partitioning

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec
import io.github.dejarol.arcgis.spark.connector.read.config.QueryLayerConfig
import org.apache.spark.sql.catalyst.util.CaseInsensitiveMap
import org.scalatest.{Inspectors, OptionValues}

class SingleArcgisPartitionSpec
  extends BasicSpec
    with Inspectors
      with OptionValues {

  describe(anInstanceOf[SingleArcgisPartition]) {
    describe(SHOULD) {
      describe("generate a sequence of queries") {
        it("when the featureCount is lower than the fetchSize") {

          val actual = SingleArcgisPartition(
            20, 50, QueryLayerConfig(
              CaseInsensitiveMap(
                Map.empty
              )
            )
          ).parametersForPartitionQueries

          actual should have size 1
          actual.head.resultOffset shouldBe empty
          actual.head.resultRecordCount shouldBe empty
        }

        it("when the featureCount is greater than the fetchSize") {

          val generatedQueries = SingleArcgisPartition(
            12, 5, QueryLayerConfig(
              CaseInsensitiveMap(
                Map(
                  QueryLayerConfig.WHERE_KEY -> "name = 'john'"
                )
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
}
