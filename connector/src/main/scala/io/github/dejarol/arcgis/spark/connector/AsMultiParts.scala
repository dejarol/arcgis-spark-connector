package io.github.dejarol.arcgis.spark.connector

import sttp.client4.BasicBodyPart
import sttp.model.Part

trait AsMultiParts {

  def parts(): Seq[Part[BasicBodyPart]]
}
