package io.github.dejarol.arcgis.spark.connector.core.config

/**
 * TODO
 */
object PropertyConversions {

  /**
   * TODO
   */
  object ToInteger extends PropertyConversion[Int] {
    override def apply(value: String): Int = Integer.parseInt(value)
  }
}
