package io.github.dejarol.arcgis.spark.connector.read.config

import io.github.dejarol.arcgis.spark.connector.core.config.{BaseConfig, PropertyConversions}

import java.util

/**
 * TODO
 * @param properties configuration entries keyed by property name
 */
case class PartitioningConfig(override protected val properties: util.Map[String, String])
  extends BaseConfig(properties) {

  import PartitioningConfig._

  /**
   * TODO
   * @return
   */
  def maxRecordsPerQuery: Int = {

    getAs[Int](
      MAX_RECORDS_PER_QUERY_KEY,
      PropertyConversions.ToInteger,
      MAX_RECORDS_PER_QUERY_DEFAULT
    )
  }
}

object PartitioningConfig {

  final val MAX_RECORDS_PER_QUERY_KEY: String = "maxRecordsPerQuery"
  final val MAX_RECORDS_PER_QUERY_DEFAULT: Int = 50
}
