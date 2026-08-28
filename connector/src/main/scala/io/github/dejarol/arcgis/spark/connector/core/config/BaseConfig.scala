package io.github.dejarol.arcgis.spark.connector.core.config

import io.github.dejarol.arcgis.spark.connector.core.{EmptyOrNonEmpty, JavaCollectionsUtils}

import java.util
import scala.util.Try

/**
 * Map-backed configuration that looks up, converts, and filters string properties.
 *
 * @param properties configuration entries keyed by property name
 * @since 0.1.0
 */
class BaseConfig(protected val properties: util.Map[String, String])
  extends EmptyOrNonEmpty {

  import BaseConfig._

  /**
   * Reports whether this configuration contains no properties.
   *
   * @return `true` if the property map is empty
   * @since 0.1.0
   */
  final def isEmpty: Boolean = properties.isEmpty

  /**
   * Reports whether a property with the given key is present.
   *
   * @param key property name to look up
   * @return `true` if this configuration contains `key`
   * @since 0.1.0
   */
  final def containsKey(key: String): Boolean = properties.containsKey(key)

  /**
   * Looks up a property as an optional string.
   *
   * @param key property name to look up
   * @return the property value, or `None` if `key` is missing
   * @since 0.1.0
   */
  protected[config] final def get(key: String): Option[String] = {

    if (containsKey(key)) {
      Some(properties.get(key))
    } else {
      None
    }
  }

  /**
   * Looks up a property, falling back to a default when the key is missing.
   *
   * @param key          property name to look up
   * @param defaultValue value returned when `key` is missing
   * @return the property value, or `defaultValue` if `key` is missing
   * @since 0.1.0
   */
  protected[config] final def getOrElse(
                                         key: String,
                                         defaultValue: String
                                       ): String = {

    get(key).getOrElse(defaultValue)
  }

  /**
   * Looks up a required property.
   *
   * @param key property name to look up
   * @return the property value
   * @throws NoSuchPropertyException if `key` is missing
   * @since 0.1.0
   */
  protected[config] final def unsafelyGet(key: String): String = {

    get(key).getOrElse {
      throw new NoSuchPropertyException(key)
    }
  }

  /**
   * Looks up a property and converts it to type `T`.
   *
   * @param key        property name to look up
   * @param conversion conversion applied to the raw string value
   * @tparam T converted value type
   * @return the converted value, or `None` if `key` is missing
   * @since 0.1.0
   */
  protected[config] final def getAs[T](
                                        key: String,
                                        conversion: PropertyConversion[T]
                                      ): Option[T] = {

    get(key).map {
      v => convertPropertyValue(
        key, v, conversion
      )
    }
  }

  /**
   * TODO
   * @param key
   * @param conversion
   * @param default
   * @tparam T
   * @return
   */
  protected[config] final def getAs[T](
                                        key: String,
                                        conversion: PropertyConversion[T],
                                        default: T
                                      ): T = {

    getAs(key, conversion).getOrElse(default)
  }

  /**
   * Looks up a required property and converts it to type `T`.
   *
   * @param key        property name to look up
   * @param conversion conversion applied to the raw string value
   * @tparam T converted value type
   * @return the converted value
   * @since 0.1.0
   */
  protected[config] final def unsafelyGetAs[T](
                                                key: String,
                                                conversion: PropertyConversion[T]
                                              ): T = {

    convertPropertyValue(
      key, unsafelyGet(key), conversion
    )
  }

  /**
   * Returns properties whose keys start with the given prefix, with the prefix stripped.
   *
   * @param prefix key prefix to match
   * @return a map of matching entries keyed without `prefix`
   * @since 0.1.0
   */
  protected[config] final def propertiesStartingWithPrefix(prefix: String): util.Map[String, String] = {

    JavaCollectionsUtils.filterMapByPrefix(
      properties, prefix
    )
  }
}

object BaseConfig {

  /**
   * Converts a raw property string to type `T`.
   *
   * @param key        property name used in conversion-error messages
   * @param value      raw property string
   * @param conversion conversion applied to `value`
   * @tparam T converted value type
   * @return the converted value
   * @throws PropertyConversionException if `conversion` fails
   * @since 0.1.0
   */
  private def convertPropertyValue[T](
                                       key: String,
                                       value: String,
                                       conversion: PropertyConversion[T]
                                     ): T = {

    Try {
      conversion.apply(value)
    }.toEither match {
      case Right(value) => value
      case Left(cause) => throw new PropertyConversionException(
        key, conversion, cause
      )
    }
  }
}
