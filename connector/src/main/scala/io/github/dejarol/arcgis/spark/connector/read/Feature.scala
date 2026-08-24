package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.core.models.Geometry

case class Feature(
                    attributes: Map[String, Any],
                    geometry: Option[Geometry]
                  )
