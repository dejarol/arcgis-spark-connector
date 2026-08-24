package io.github.dejarol.arcgis.spark.connector.core.config

import io.github.dejarol.arcgis.spark.connector.core.JavaCollectionsUtils

import java.util
import scala.util.Try

/**
 * TODO
 * @param properties
 */
class BaseConfig(protected val properties: util.Map[String, String]) {

  import BaseConfig._

  /**
   * TODO
   * @return
   */
  final def isEmpty: Boolean = properties.isEmpty

  /** TODO
   * @param key
   * @return
   */

  final def containsKey(key: String): Boolean = properties.containsKey(key)

  /**
   * TODO
   * @param key
   * @return
   */
  protected[config] final def get(key: String): Option[String] = {

    if (containsKey(key)) {
      Some(properties.get(key))
    } else {
      None
    }
  }

  /**
   * TODO
   * @param key
   * @param defaultValue
   * @return
   */
  protected[config] final def getOrElse(
                                         key: String,
                                         defaultValue: String
                                       ): String = {

    get(key).getOrElse(defaultValue)
  }

  /**
   * TODO
   * @param key
   * @return
   */
  protected[config] final def unsafelyGet(key: String): String = {

    get(key).getOrElse {
      throw new NoSuchPropertyException(key)
    }
  }

  /**
   * TODO
   * @param key
   * @param conversion
   * @tparam T
   * @return
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
   * @tparam T
   * @return
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
   * TODO
   * @param prefix
   * @return
   */
  protected[config] final def propertiesStartingWithPrefix(prefix: String): util.Map[String, String] = {

    JavaCollectionsUtils.filterMapByPrefix(
      properties, prefix
    )
  }
}

object BaseConfig {

  /**
   * TODO
   * @param key
   * @param value
   * @param conversion
   * @tparam T
   * @return
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
