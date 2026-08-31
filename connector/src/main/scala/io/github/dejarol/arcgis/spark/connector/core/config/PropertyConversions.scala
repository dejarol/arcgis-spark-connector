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
   * @param internal
   * @param internalDescription
   * @tparam T
   */
  private class ToListOf[T](
                             private val internal: String => T,
                             private val internalDescription: String
                           )

    extends PropertyConversion[Seq[T]] {
    override def apply(value: String): Seq[T] = value.split(',').map(internal)
    override def targetTypeDescription: String = s"list of $internalDescription"
  }

  /**
   * TODO
   */
  object ToBoolean extends PropertyConversion[Boolean] {
    override def apply(value: String): Boolean = java.lang.Boolean.parseBoolean(value)
    override def targetTypeDescription: String = "boolean"
  }

  /**
   * TODO
   * @return
   */
  def toListOfString(): PropertyConversion[Seq[String]] = new ToListOf[String](_.trim, "string")

  /**
   * TODO
   * @return
   */
  def toListOfInt(): PropertyConversion[Seq[Int]] = new ToListOf[Int](_.trim.toInt, "integer")
}
