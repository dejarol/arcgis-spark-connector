package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.config.{BaseConfig, PropertyConversions}
import org.apache.spark.sql.catalyst.util.CaseInsensitiveMap

/**
 * Partitioning options that control how a feature layer scan is split across Spark partitions.
 *
 * @param properties configuration entries keyed by property name
 * @since 0.1.0
 */
case class PartitioningConfig(override protected val properties: CaseInsensitiveMap[String])
  extends BaseConfig(properties) {

  import PartitioningConfig._

  /**
   * Returns the number of features to fetch per query page.
   *
   * @return the configured fetch size, or [[PartitioningConfig.FETCH_SIZE_DEFAULT]] when unset
   * @since 0.1.0
   */
  def fetchSize: Int = {

    getAs[Int](
      FETCH_SIZE_KEY,
      PropertyConversions.ToInteger,
      FETCH_SIZE_DEFAULT
    )
  }

  /**
   * Returns the desired number of Spark partitions, if set.
   *
   * @return the partition count, or `None` if unset
   * @since 0.1.0
   */
  def numPartitions: Option[Int] = {

    getAs[Int](
      NUM_PARTITIONS_KEY,
      PropertyConversions.ToInteger
    )
  }
}

object PartitioningConfig {

  /**
   * Property key for the number of features to fetch per query page.
   *
   * @since 0.1.0
   */
  final val FETCH_SIZE_KEY: String = "fetchSize"

  /**
   * Default fetch size used when [[FETCH_SIZE_KEY]] is unset.
   *
   * @since 0.1.0
   */
  final val FETCH_SIZE_DEFAULT: Int = 50

  /**
   * Property key for the desired number of Spark partitions.
   *
   * @since 0.1.0
   */
  final val NUM_PARTITIONS_KEY: String = "numPartitions"
}
