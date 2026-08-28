package io.github.dejarol.arcgis.spark.connector.core.http

import sttp.client4.BasicBodyPart
import sttp.model.Part

/**
 * Types that can encode themselves as STTP multipart form parts.
 *
 * @since 0.1.0
 */
trait AsMultiParts {

  /**
   * Returns the multipart form parts that represent this instance.
   *
   * @return STTP multipart parts ready to attach to a request
   * @since 0.1.0
   */
  def parts(): Seq[Part[BasicBodyPart]]
}
