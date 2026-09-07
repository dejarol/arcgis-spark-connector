package io.github.dejarol.arcgis.spark.connector.read.encoding

import org.apache.spark.unsafe.types.UTF8String
import org.json4s.JsonAST._

import java.lang

/**
 * Factory for encoders that convert optional ArcGIS feature attributes into Spark SQL types.
 *
 * @since 0.1.0
 */
object AttributeValueEncoders {

  /**
   * Encodes ArcGIS date attributes as Spark timestamps (epoch milliseconds).
   *
   * @since 0.1.0
   */
  private object DateEncoder
    extends AttributeValueEncoder[lang.Long] {

    /**
     * Encodes a numeric JSON value as milliseconds since epoch.
     *
     * @param value JSON attribute value
     * @return milliseconds since epoch, or `null` for JSON null
     * @throws IllegalArgumentException if `value` is not a number or null
     * @since 0.1.0
     */
    override def apply(value: JValue): lang.Long = {

      value match {
        case JLong(num) => num.longValue() * 1000
        case JInt(num) => num.longValue() * 1000
        case JDecimal(num) => num.longValue() * 1000
        case JNull => null
        case _ => throw new IllegalArgumentException(
          s"Input value was supposed to be a long, but was ${value.getClass.getName}"
        )
      }
    }
  }

  /**
   * Encodes ArcGIS numeric attributes as Java doubles.
   *
   * @since 0.1.0
   */
  private object DoubleEncoder
    extends AttributeValueEncoder[lang.Double] {

    /**
     * Encodes a numeric JSON value as a Java `Double`.
     *
     * @param value JSON attribute value
     * @return the numeric value as a `Double`, or `null` for JSON null
     * @throws IllegalArgumentException if `value` is not a number or null
     * @since 0.1.0
     */
    override def apply(value: JValue): lang.Double = {

      value match {
        case JDouble(num) => num
        case JInt(num) => num.doubleValue()
        case JLong(num) => num.doubleValue()
        case JDecimal(num) => num.doubleValue()
        case JNull => null
        case _ => throw new IllegalArgumentException(
          s"Input value was supposed to be a ${classOf[JNumber].getName}, but was ${value.getClass.getName}"
        )
      }
    }
  }

  /**
   * Encodes ArcGIS integer attributes as Java integers.
   *
   * @since 0.1.0
   */
  private object IntegerEncoder
    extends AttributeValueEncoder[lang.Integer] {

    /**
     * Encodes a numeric JSON value as a Java `Integer`.
     *
     * @param value JSON attribute value
     * @return the numeric value as an `Integer`, or `null` for JSON null
     * @throws IllegalArgumentException if `value` is not a number or null
     * @since 0.1.0
     */
    override def apply(value: JValue): Integer = {

      value match {
        case JInt(num) => num.intValue()
        case JLong(num) => num.intValue()
        case JDecimal(num) => num.intValue()
        case JNull => null
        case _ => throw new IllegalArgumentException(
          s"Input value was supposed to be a, but was ${value.getClass.getName}"
        )
      }
    }
  }

  /**
   * Encodes ArcGIS string attributes as Spark UTF8 strings.
   *
   * @since 0.1.0
   */
  private object StringEncoder
    extends AttributeValueEncoder[UTF8String] {

    /**
     * Encodes a JSON string as a Spark `UTF8String`.
     *
     * @param value JSON attribute value
     * @return the string as UTF8, or `null` for JSON null
     * @throws IllegalArgumentException if `value` is not a string or null
     * @since 0.1.0
     */
    override def apply(value: JValue): UTF8String = {

      value match {
        case JString(str) => UTF8String.fromString(str)
        case JNull => null
        case _ => throw new IllegalArgumentException(
          s"Input value was supposed to be a string, but was ${value.getClass.getName}"
        )
      }
    }
  }

  /**
   * Creates an encoder for ArcGIS date attributes.
   *
   * @return an encoder that maps missing values to `null` and present epoch seconds to milliseconds
   * @since 0.1.0
   */
  def forDate(): AttributeValueEncoder[lang.Long] = DateEncoder

  /**
   * Creates an encoder for ArcGIS double attributes.
   *
   * @return an encoder that maps missing values to `null` and present doubles unchanged
   * @since 0.1.0
   */
  def forDouble(): AttributeValueEncoder[lang.Double] = DoubleEncoder

  /**
   * Creates an encoder for ArcGIS integer attributes.
   *
   * @return an encoder that maps missing values to `null` and present integers unchanged
   * @since 0.1.0
   */
  def forInteger(): AttributeValueEncoder[lang.Integer] = IntegerEncoder

  /**
   * Creates an encoder for ArcGIS string attributes.
   *
   * @return an encoder that maps missing values to `null` and present strings to UTF8
   * @since 0.1.0
   */
  def forString(): AttributeValueEncoder[UTF8String] = StringEncoder
}
