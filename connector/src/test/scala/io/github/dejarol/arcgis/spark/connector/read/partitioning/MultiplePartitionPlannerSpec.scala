package io.github.dejarol.arcgis.spark.connector.read.partitioning

import io.github.dejarol.arcgis.spark.connector.core.ConfigSpec
import io.github.dejarol.arcgis.spark.connector.read.config.QueryLayerConfig
import org.scalatest.{Inspectors, OptionValues}

class MultiplePartitionPlannerSpec
  extends ConfigSpec
    with Inspectors
    with OptionValues {

  describe(anInstanceOf[MultiplePartitionPlanner]) {
    describe(SHOULD) {
      describe("plan multiple partitions") {
        it("spanning the whole featureCount") {

          val numPartitions = 3
          val actual = MultiplePartitionPlanner(
            250, numPartitions, 50, QueryLayerConfig(EMPTY_CIMAP)
          ).plan()

          actual should have size numPartitions
          actual.map(_.partitionId) should contain theSameElementsAs (0 until numPartitions).toList

          val expected = Seq(
            ((0, 50), (50, 34)),
            ((84, 50), (134, 34)),
            ((168, 50), (218, 32))
          )

          forAll(actual.zip(expected)) {
            case (partition, ((q1Offset, q1RecCount), (q2Offset, q2RecCount))) =>
              partition.parametersForPartitionQueries should have size 2
              partition.parametersForPartitionQueries.head.resultOffset.value shouldBe q1Offset
              partition.parametersForPartitionQueries.head.resultRecordCount.value shouldBe q1RecCount
              partition.parametersForPartitionQueries(1).resultOffset.value shouldBe q2Offset
              partition.parametersForPartitionQueries(1).resultRecordCount.value shouldBe q2RecCount
          }
        }
      }
    }
  }
}
