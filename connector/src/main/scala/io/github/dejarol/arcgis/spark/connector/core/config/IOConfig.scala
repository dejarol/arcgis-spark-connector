package io.github.dejarol.arcgis.spark.connector.core.config

import java.util
import scala.util.Try

/**
 * TODO
 * @param properties
 */
class IOConfig(
                protected val properties: util.Map[String, String]
              ) {

  import IOConfig._

  /**
   * TODO
   * @param key
   * @return
   */
  def get(key: String): Option[String] = {

    if (properties.containsKey(key)) {
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
  def getOrElse(
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
  def unsafelyGet(key: String): String = {

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
  def getAs[T](
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
  def unsafelyGetAs[T](
                        key: String,
                        conversion: PropertyConversion[T]
                      ): T = {

    convertPropertyValue(
      key, unsafelyGet(key), conversion
    )
  }
}

object IOConfig {

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
        s"Failed to convert property value for key '$key'", cause
      )
    }
  }
}
