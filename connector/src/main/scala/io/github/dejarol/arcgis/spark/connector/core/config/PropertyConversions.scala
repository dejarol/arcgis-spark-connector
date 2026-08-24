package io.github.dejarol.arcgis.spark.connector.core.config

import sttp.model.Uri

/**
 * TODO
 */
object PropertyConversions {

  /**
   * TODO
   */
  object ToInteger extends PropertyConversion[Int] {
    override def apply(value: String): Int = Integer.parseInt(value)
    override def targetTypeDescription: String = "integer"
  }

  /**
   * TODO
   */
  object ToUri extends PropertyConversion[Uri] {
    override def apply(value: String): Uri = Uri.unsafeParse(value)
    override def targetTypeDescription: String = "URI"
  }

  /**
   * TODO
   */
  object ToList extends PropertyConversion[Seq[String]] {
    override def apply(value: String): Seq[String] = value.split(',').map(_.trim)
    override def targetTypeDescription: String = "list of strings (comma separated)"
  }

  /**
   * TODO
   */
  object ToBoolean extends PropertyConversion[Boolean] {
    override def apply(value: String): Boolean = java.lang.Boolean.parseBoolean(value)
    override def targetTypeDescription: String = "boolean"
  }
}
