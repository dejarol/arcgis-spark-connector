package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.core.http.AsMultiParts
import sttp.client4.{BasicBodyPart, multipart}
import sttp.model.Part

/**
 * Parameters sent as multipart form fields to an ArcGIS feature layer query.
 *
 * @param where             optional ArcGIS `where` clause
 * @param objectIDs         TODO
 * @param outFields         optional output field names
 * @param returnGeometry    whether the query should include geometry
 * @param outSR             optional output spatial reference WKID
 * @param resultOffset      optional result offset for pagination
 * @param resultRecordCount optional maximum number of records to return
 * @param returnCountOnly   whether the query should return only the feature count
 * @param token             TODO
 * @since 0.1.0
 */
case class QueryLayerParameters(
                                 where: Option[String] = None,
                                 objectIDs: Option[Seq[Int]] = None,
                                 outFields: Option[Seq[String]] = None,
                                 returnGeometry: Option[Boolean] = None,
                                 outSR: Option[Int] = None,
                                 resultOffset: Option[Int] = None,
                                 resultRecordCount: Option[Int] = None,
                                 returnCountOnly: Option[Boolean] = None,
                                 token: Option[String] = None
                               )
  extends AsMultiParts {

  import QueryLayerParameters._

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
   * Returns a copy of these parameters with the given result offset and record count.
   *
   * @param offset result offset applied to the query
   * @param count  maximum number of records to return
   * @return a copy of this instance with `resultOffset` and `resultRecordCount` set
   * @since 0.1.0
   */
  def withResultOffsetAndRecordCount(
                                      offset: Int,
                                      count: Int
                                    ): QueryLayerParameters =
    {
      this.copy(
        resultOffset = Some(offset),
        resultRecordCount = Some(count)
      )
    }

  /**
   * Builds the multipart part for the `where` clause.
   *
   * @return a part named `where`, using [[QueryLayerParameters.DEFAULT_WHERE]] when unset
   * @since 0.1.0
   */
  private def whereMultiPart: Part[BasicBodyPart] = multipart("where", where.getOrElse(DEFAULT_WHERE))

  /**
   * TODO
   * @return
   */
  private def objectIDsMultiPart: Option[Part[BasicBodyPart]] = {

    objectIDs.map {
      ids => multipart("objectIds", ids.mkString(","))
    }
  }

  /**
   * TODO
   * @return
   */
  private def tokenMultiPart: Option[Part[BasicBodyPart]] = {

    token.map {
      token => multipart("token", token)
    }
  }

  /**
   * Builds the multipart parts for a count-only query.
   *
   * @return parts for `returnCountOnly` and `where`
   * @since 0.1.0
   */
  private def partsForReturnCountOnly(): Seq[Part[BasicBodyPart]] = {

    // [1.1] Build the default parts
    val defaults = Seq(
      multipart("returnCountOnly", "true"),
      whereMultiPart
    )

    // [1.2] Build the optional parts
    val optionals = Seq(
      objectIDsMultiPart,
      tokenMultiPart
    ).collect {
      case Some(value) => value
    }

    defaults ++ optionals
  }

  /**
   * Builds the multipart parts that are always sent for a feature query.
   *
   * @return parts for `where`, `outFields`, and `returnGeometry`
   * @since 0.1.0
   */
  private def defaultParts(): Seq[Part[BasicBodyPart]] = {

    Seq(
      whereMultiPart,
      multipart("outFields", outFields.map(_.mkString(",")).getOrElse(DEFAULT_OUT_FIELDS)),
      multipart("returnGeometry", String.valueOf(returnGeometry.getOrElse(DEFAULT_RETURN_GEOMETRY)))
    )
  }

  /**
   * Builds the optional multipart parts for a feature query.
   *
   * @return optional parts for `outSR`, `resultOffset`, and `resultRecordCount`
   * @since 0.1.0
   */
  private def optionalParts(): Seq[Option[Part[BasicBodyPart]]] = {

    Seq(
      objectIDsMultiPart,
      tokenMultiPart,
      outSR.map(sr => multipart("outSR", String.valueOf(sr))),
      resultOffset.map(offset => multipart("resultOffset", String.valueOf(offset))),
      resultRecordCount.map(count => multipart("resultRecordCount", String.valueOf(count))),
    )
  }
}

object QueryLayerParameters {

  /**
   * Default ArcGIS `where` clause used when none is provided.
   *
   * @since 0.1.0
   */
  final val DEFAULT_WHERE: String = "1=1"

  /**
   * Default `outFields` value used when none is provided.
   *
   * @since 0.1.0
   */
  final val DEFAULT_OUT_FIELDS: String = "*"

  /**
   * Default `returnGeometry` value used when none is provided.
   *
   * @since 0.1.0
   */
  final val DEFAULT_RETURN_GEOMETRY: Boolean = false

  /**
   * Builds query parameters that request only the feature count.
   *
   * @param where optional ArcGIS `where` clause
   * @param objectIDs TODO
   * @param token TODO
   * @return parameters with `returnCountOnly` set to `true`
   * @since 0.1.0
   */
  def returnCountOnly(
                       where: Option[String],
                       objectIDs: Option[Seq[Int]],
                       token: Option[String]
                     ): QueryLayerParameters = {

    QueryLayerParameters(
      where = where,
      returnCountOnly = Some(true),
      objectIDs = objectIDs,
      token = token
    )
  }
}
