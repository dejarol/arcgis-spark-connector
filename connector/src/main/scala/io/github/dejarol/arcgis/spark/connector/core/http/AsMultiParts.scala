package io.github.dejarol.arcgis.spark.connector.core.http

import sttp.client4.BasicBodyPart
import sttp.model.Part

/**
 * TODO
 */
trait AsMultiParts {

  /**
   * TODO
   * @return
   */
  def parts(): Seq[Part[BasicBodyPart]]
}
