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
  def fetchSize: Int = {

    getAs[Int](
      FETCH_SIZE_KEY,
      PropertyConversions.ToInteger,
      FETCH_SIZE_DEFAULT
    )
  }
}

object PartitioningConfig {

  final val FETCH_SIZE_KEY: String = "fetchSize"
  final val FETCH_SIZE_DEFAULT: Int = 50
}
