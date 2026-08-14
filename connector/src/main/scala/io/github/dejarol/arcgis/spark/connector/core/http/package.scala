package io.github.dejarol.arcgis.spark.connector.core

import sttp.client4.{PartialRequest, Request, Response, basicRequest}
import sttp.model.Uri

/**
 * TODO
 */
package object http {

  type PRType = PartialRequest[Either[String, String]]
  type RType = Request[Either[String, String]]
  type RespType = Response[Either[String, String]]

  /**
   * TODO
   * @return
   */
  def initialRequest(): PRType = basicRequest

  /**
   * TODO
   * @param string
   * @return
   */
  def uriFromString(string: String): Uri = Uri.unsafeParse(string)
}
