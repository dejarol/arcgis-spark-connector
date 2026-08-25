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
case class QueryParameters(
                            where: Option[String] = None,
                            outFields: Option[Seq[String]] = None,
                            returnGeometry: Option[Boolean] = None,
                            outSR: Option[Int] = None,
                            resultOffset: Option[Int] = None,
                            resultRecordCount: Option[Int] = None
                          )
  extends AsMultiParts {

  import QueryParameters._

  override def parts(): Seq[Part[BasicBodyPart]] = {

    // Combine default and optional parts
    defaultParts() ++ optionalParts().collect {
      case Some(part) => part
    }
  }

  /**
   * TODO
   * @return
   */
  private def defaultParts(): Seq[Part[BasicBodyPart]] = {

    Seq(
      multipart("where", where.getOrElse(DEFAULT_WHERE)),
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

object QueryParameters {

  final val DEFAULT_WHERE: String = "1=1"
  final val DEFAULT_OUT_FIELDS: String = "*"
  final val DEFAULT_RETURN_GEOMETRY: Boolean = false
}
