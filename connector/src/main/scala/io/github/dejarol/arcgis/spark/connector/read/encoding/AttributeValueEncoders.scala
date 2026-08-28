package io.github.dejarol.arcgis.spark.connector.read.encoding

import org.apache.spark.unsafe.types.UTF8String

import java.lang
import scala.reflect.ClassTag

/**
 * Factory for encoders that convert optional ArcGIS feature attributes into Spark SQL types.
 *
 * @since 0.1.0
 */
object AttributeValueEncoders {

  /**
   * Encoder for a single atomic ArcGIS attribute type.
   *
   * Maps a missing attribute to a default and converts a present value of the expected Scala type
   * into Spark's internal representation.
   *
   * @param default            value returned when the attribute is absent
   * @param internalConversion conversion from the expected Scala type to the Spark representation
   * @since 0.1.0
   */
  private class AtomicAttributeValueEncoder[I: ClassTag, T](
                                                            private val default: T,
                                                            private val internalConversion: I => T
                                                          )
    extends AttributeValueEncoder[T] {

    /**
     * Encodes an optional ArcGIS attribute into a Spark value.
     *
     * @param value optional REST attribute; `None` maps to the encoder default
     * @return the Spark representation of the attribute, or the default if absent
     * @throws IllegalArgumentException if `value` is present but not of the expected type
     * @since 0.1.0
     */
    override def apply(value: Option[Any]): T = {

      value match {
        case None => default
        case Some(v: I) => internalConversion(v)
        case Some(other) =>

          val internalTypeDescription = implicitly[ClassTag[I]].runtimeClass.getName
          throw new IllegalArgumentException(
            s"Input value was supposed to be a $internalTypeDescription, " +
              s"but was ${other.getClass.getName}"
          )
      }
    }
  }

  /**
   * Creates an encoder for ArcGIS double attributes.
   *
   * @return an encoder that maps missing values to `null` and present doubles unchanged
   * @since 0.1.0
   */
  def forDouble(): AttributeValueEncoder[lang.Double] = {

    new AtomicAttributeValueEncoder[Double, lang.Double](null, identity)
  }

  /**
   * Creates an encoder for ArcGIS integer attributes.
   *
   * @return an encoder that maps missing values to `null` and present integers unchanged
   * @since 0.1.0
   */
  def forInteger(): AttributeValueEncoder[lang.Integer] = {

    new AtomicAttributeValueEncoder[Integer, lang.Integer](null, identity)
  }

  /**
   * Creates an encoder for ArcGIS string attributes.
   *
   * @return an encoder that maps missing values to `null` and present strings to UTF8
   * @since 0.1.0
   */
  def forString(): AttributeValueEncoder[UTF8String] = {

    new AtomicAttributeValueEncoder[String, UTF8String](null, UTF8String.fromString)
  }
}
