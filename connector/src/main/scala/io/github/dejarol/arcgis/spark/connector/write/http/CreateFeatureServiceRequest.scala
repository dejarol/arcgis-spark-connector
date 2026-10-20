package io.github.dejarol.arcgis.spark.connector.write.http

import io.github.dejarol.arcgis.spark.connector.write.models.CreateFeatureServiceParameters

case class CreateFeatureServiceRequest(
                                        username: String,
                                        token: String,
                                        createParameters: CreateFeatureServiceParameters
                                      )
