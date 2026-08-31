package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.config.{BaseConfig, PropertyConversions}
import org.apache.spark.sql.catalyst.util.CaseInsensitiveMap

/**
 * TODO
 * @param properties configuration entries keyed by property name
 */
case class PartitioningConfig(override protected val properties: CaseInsensitiveMap[String])
  extends BaseConfig(properties) {

  import PartitioningConfig._

  /**
   * TODO
   * @return
   */
  def fetchSize: Int = {

    getAs[Int](
      FETCH_SIZE_KEY,
      PropertyConversions.ToInteger,
      FETCH_SIZE_DEFAULT
    )
  }

  /**
   * TODO
   * @return
   */
  def numPartitions: Option[Int] = {

    getAs[Int](
      NUM_PARTITIONS_KEY,
      PropertyConversions.ToInteger
    )
  }
}

object PartitioningConfig {

  final val FETCH_SIZE_KEY: String = "fetchSize"
  final val FETCH_SIZE_DEFAULT: Int = 50

  final val NUM_PARTITIONS_KEY: String = "numPartitions"
}
