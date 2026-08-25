package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.core.http.AsMultiParts
import sttp.client4.{BasicBodyPart, multipart}
import sttp.model.Part

/**
 * TODO
 * @param where
 * @param outFields
 * @param returnGeometry
 * @param outSR
 * @param resultOffset
 * @param resultRecordCount
 */
case class FeatureLayerQueryParameters(
                                        where: Option[String] = None,
                                        outFields: Option[Seq[String]] = None,
                                        returnGeometry: Option[Boolean] = None,
                                        outSR: Option[Int] = None,
                                        resultOffset: Option[Int] = None,
                                        resultRecordCount: Option[Int] = None,
                                        returnCountOnly: Option[Boolean] = None
                                      )
  extends AsMultiParts {

  import FeatureLayerQueryParameters._

  override def parts(): Seq[Part[BasicBodyPart]] = {

    // [1.1] Evaluate if return count only is enabled
    if (returnCountOnly.getOrElse(false)) {
      partsForReturnCountOnly()
    } else {
      // [1.2] Combine default and optional parts
      defaultParts() ++ optionalParts().collect {
        case Some(part) => part
      }
    }
  }

  /**
   * TODO
   * @param offset
   * @param count
   * @return
   */
  def withResultOffsetAndRecordCount(
                                      offset: Int,
                                      count: Int
                                    ): FeatureLayerQueryParameters =
    {
      this.copy(
        resultOffset = Some(offset),
        resultRecordCount = Some(count)
      )
    }

  /**
   * TODO
   * @return
   */
  private def whereMultiPart: Part[BasicBodyPart] = multipart("where", where.getOrElse(DEFAULT_WHERE))

  /**
   * TODO
   * @return
   */
  private def partsForReturnCountOnly(): Seq[Part[BasicBodyPart]] = {

    Seq(
      multipart("returnCountOnly", "true"),
      whereMultiPart
    )
  }

  /**
   * TODO
   * @return
   */
  private def defaultParts(): Seq[Part[BasicBodyPart]] = {

    Seq(
      whereMultiPart,
      multipart("outFields", outFields.map(_.mkString(",")).getOrElse(DEFAULT_OUT_FIELDS)),
      multipart("returnGeometry", String.valueOf(returnGeometry.getOrElse(DEFAULT_RETURN_GEOMETRY)))
    )
  }

  /**
   * TODO
   * @return
   */
  private def optionalParts(): Seq[Option[Part[BasicBodyPart]]] = {

    Seq(
      outSR.map(sr => multipart("outSR", String.valueOf(sr))),
      resultOffset.map(offset => multipart("resultOffset", String.valueOf(offset))),
      resultRecordCount.map(count => multipart("resultRecordCount", String.valueOf(count)))
    )
  }
}

object FeatureLayerQueryParameters {

  final val DEFAULT_WHERE: String = "1=1"
  final val DEFAULT_OUT_FIELDS: String = "*"
  final val DEFAULT_RETURN_GEOMETRY: Boolean = false

  /**
   * TODO
   * @return
   */
  def returnCountOnly(where: Option[String]): FeatureLayerQueryParameters = {

    FeatureLayerQueryParameters(
      where = where,
      returnCountOnly = Some(true)
    )
  }
}
