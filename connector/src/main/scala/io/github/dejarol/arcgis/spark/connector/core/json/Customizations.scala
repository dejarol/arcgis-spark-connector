package io.github.dejarol.arcgis.spark.connector.core.json

import io.github.dejarol.arcgis.spark.connector.core.EnumWithAPIName
import io.github.dejarol.arcgis.spark.connector.core.utils.Enums
import org.json4s.{CustomSerializer, JString}

import scala.reflect.ClassTag

/**
 * TODO
 */
object Customizations {

  /**
   * TODO
   * @tparam E
   * @return
   */
  def serializerForEnumWithAPIName[E <: Enum[E] with EnumWithAPIName: ClassTag](): CustomSerializer[E] = {

    new CustomSerializer[E](format => (
      {
        case JString(s) if Enums.exists[E](s, (v: E, n: String) => v.matchesAPIName(n)) =>
          Enums.unsafeValueOfEnum[E](s, (v: E, n: String) => v.matchesAPIName(n))
      },
      {
        case e: E => JString(e.getAPIName)
      }
    ))
  }
}
