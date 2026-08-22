package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.AsMultiParts
import sttp.client4.{BasicBodyPart, multipart}
import sttp.model.Part

case class QueryParameters(
                            where: Option[String] = None,
                            outFields: Option[Seq[String]] = None
                          )
  extends AsMultiParts {

  import QueryParameters._

  override def parts(): Seq[Part[BasicBodyPart]] = {

    Seq(
      multipart("where", where.getOrElse(DEFAULT_WHERE)),
      multipart("outFields", outFields.map(_.mkString(",")).getOrElse(DEFAULT_OUT_FIELDS))
    )
  }
}

object QueryParameters {

  final val DEFAULT_WHERE: String = "1=1"
  final val DEFAULT_OUT_FIELDS: String = "*"
}
