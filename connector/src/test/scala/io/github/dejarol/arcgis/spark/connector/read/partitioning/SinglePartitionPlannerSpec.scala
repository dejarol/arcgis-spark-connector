package io.github.dejarol.arcgis.spark.connector.read.partitioning

import io.github.dejarol.arcgis.spark.connector.core.ConfigSpec
import io.github.dejarol.arcgis.spark.connector.read.config.QueryLayerConfig
import org.scalatest.{Inspectors, OptionValues}

class SinglePartitionPlannerSpec
  extends ConfigSpec
    with Inspectors
      with OptionValues {

  describe(anInstanceOf[SinglePartitionPlanner]) {
    describe(SHOULD) {
      describe("plan a single partition") {
        it("with just 1 query, without resultOffset and resultRecordCount") {

          val actual = SinglePartitionPlanner(
            20, 50, QueryLayerConfig(EMPTY_CIMAP)
          ).plan()

          actual should have size 1
          val partition = actual.head
          partition.parametersForPartitionQueries should have size 1
          val queryParameters = partition.parametersForPartitionQueries.head
          queryParameters.resultOffset shouldBe empty
          queryParameters.resultRecordCount shouldBe empty
        }

        it("with multiple queries, spanning the whole featureCount") {

          val fetchSize = 50
          val actual = SinglePartitionPlanner(
            220, fetchSize, QueryLayerConfig(EMPTY_CIMAP)
          ).plan()

          actual should have size 1
          val partition = actual.head
          partition.parametersForPartitionQueries should have size 5
          val queryParameters = partition.parametersForPartitionQueries.map {
            query => (query.resultOffset, query.resultRecordCount)
          }

          val expected = Seq(
            (0, fetchSize),
            (50, fetchSize),
            (100, fetchSize),
            (150, fetchSize),
            (200, 20)
          )

          forAll(queryParameters.zip(expected)) {
            case ((actualResultOffset, actualCount), (expectedResultOffset, expectedCount)) =>

              actualResultOffset shouldBe defined
              actualResultOffset.value shouldBe expectedResultOffset
              actualCount shouldBe defined
              actualCount.value shouldBe expectedCount
          }
        }
      }
    }
  }
}
