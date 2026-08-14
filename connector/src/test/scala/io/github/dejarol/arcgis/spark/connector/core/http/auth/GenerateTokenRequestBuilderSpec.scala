package io.github.dejarol.arcgis.spark.connector.core.http.auth

import io.github.dejarol.arcgis.spark.connector.core.http._
import sttp.client4.BasicMultipartBody
import sttp.model.Method

import java.time.Duration

class GenerateTokenRequestBuilderSpec
  extends SttpRequestBuilderSpec {

  describe(anInstanceOf[GenerateTokenRequestBuilder]) {
    describe(SHOULD) {
      it("build a request as expected") {

        val builder = GenerateTokenRequestBuilder(
          uriFromString("http://localhost:8080"),
          "john",
          "doe",
          "refValue",
          Duration.ofMinutes(10)
        )
        val actual = builder.build(initialRequest())

        actual.method shouldBe Method.POST
        actual.uri shouldBe builder.authUri
        assertBodyIsInstanceOf[BasicMultipartBody](actual.body) {
          b => assertMultiPartsMatch(
            b.parts, Map(
              "f" -> "json",
              "username" -> builder.username,
              "password" -> builder.password,
              "client" -> "referer",
              "referer" -> builder.referer,
              "expiration" -> String.valueOf(builder.duration.toMinutes)
            )
          )
        }
      }
    }
  }
}
