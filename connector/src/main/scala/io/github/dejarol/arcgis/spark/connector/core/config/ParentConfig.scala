package io.github.dejarol.arcgis.spark.connector.core.config

import io.github.dejarol.arcgis.spark.connector.core.JavaCollectionsUtils

import java.util
import java.util.stream.Collectors
import scala.util.Try

/**
 * TODO
 * @param properties
 */
class ParentConfig(
                protected val properties: util.Map[String, String]
              ) {

  import ParentConfig._

  /**
   * TODO
   * @param key
   * @return
   */
  final def get(key: String): Option[String] = {

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
  final def getOrElse(
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
  final def unsafelyGet(key: String): String = {

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
  final def getAs[T](
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
  final def unsafelyGetAs[T](
                              key: String,
                              conversion: PropertyConversion[T]
                            ): T = {

    convertPropertyValue(
      key, unsafelyGet(key), conversion
    )
  }

  final def configWithAllPropertiesStartingWithPrefix(prefix: String): ParentConfig = {

    new ParentConfig(
      JavaCollectionsUtils.filterMapByPrefix(
        properties, prefix
      )
    )
  }
}

object ParentConfig {

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
