package io.github.dejarol.arcgis.spark.connector.core.config

import sttp.model.Uri

/**
 * Built-in conversions from raw configuration strings to typed values.
 *
 * @since 0.1.0
 */
object PropertyConversions {

  /**
   * Converts a property string to an integer.
   *
   * @since 0.1.0
   */
  object ToInteger extends PropertyConversion[Int] {

    /**
     * Parses the raw string as an integer.
     *
     * @param value raw property string
     * @return the parsed integer
     * @since 0.1.0
     */
    override def apply(value: String): Int = Integer.parseInt(value)

    /**
     * Returns a short description of the target type.
     *
     * @return `"integer"`
     * @since 0.1.0
     */
    override def targetTypeDescription: String = "integer"
  }

  /**
   * Converts a property string to an STTP URI.
   *
   * @since 0.1.0
   */
  object ToUri extends PropertyConversion[Uri] {

    /**
     * Parses the raw string as a URI.
     *
     * @param value raw property string
     * @return the parsed URI
     * @since 0.1.0
     */
    override def apply(value: String): Uri = Uri.unsafeParse(value)

    /**
     * Returns a short description of the target type.
     *
     * @return `"URI"`
     * @since 0.1.0
     */
    override def targetTypeDescription: String = "URI"
  }

  /**
   * Converts a comma-separated property string into a sequence of values.
   *
   * @param internal            conversion applied to each comma-separated token
   * @param internalDescription description of the element type used in error messages
   * @since 0.1.0
   */
  private class ToListOf[T](
                             private val internal: String => T,
                             private val internalDescription: String
                           )

    extends PropertyConversion[Seq[T]] {

    /**
     * Converts a comma-separated property string to a sequence of `T`.
     *
     * @param value raw property string
     * @return the converted values
     * @since 0.1.0
     */
    override def apply(value: String): Seq[T] = value.split(',').map(internal)

    /**
     * Returns a short description of the target type.
     *
     * @return a description of the form `list of …`
     * @since 0.1.0
     */
    override def targetTypeDescription: String = s"list of $internalDescription"
  }

  /**
   * Converts a property string to a boolean.
   *
   * @since 0.1.0
   */
  object ToBoolean extends PropertyConversion[Boolean] {

    /**
     * Parses the raw string as a boolean.
     *
     * @param value raw property string
     * @return the parsed boolean
     * @since 0.1.0
     */
    override def apply(value: String): Boolean = java.lang.Boolean.parseBoolean(value)

    /**
     * Returns a short description of the target type.
     *
     * @return `"boolean"`
     * @since 0.1.0
     */
    override def targetTypeDescription: String = "boolean"
  }

  /**
   * Returns a conversion from a comma-separated string to a sequence of strings.
   *
   * @return a conversion to a sequence of strings
   * @since 0.1.0
   */
  def toListOfString: PropertyConversion[Seq[String]] = new ToListOf[String](_.trim, "string")

  /**
   * Returns a conversion from a comma-separated string to a sequence of integers.
   *
   * @return a conversion to a sequence of integers
   * @since 0.1.0
   */
  def toListOfInt: PropertyConversion[Seq[Int]] = new ToListOf[Int](_.trim.toInt, "integer")
}
