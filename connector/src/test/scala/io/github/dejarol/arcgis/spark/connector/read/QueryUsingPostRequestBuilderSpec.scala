package io.github.dejarol.arcgis.spark.connector.read

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec
import io.github.dejarol.arcgis.spark.connector.core.http.{initialRequest, uriFromString}
import sttp.client4.DefaultSyncBackend

class QueryUsingPostRequestBuilderSpec
  extends BasicSpec {

  it("a") {

    QueryUsingPostRequestBuilder(
      uriFromString("https://p3eplmys2rvchkjx.svcs.arcgis.com/P3ePLMYs2RVChkJx/ArcGIS/rest/services/ACS_Population_View_Boundaries/FeatureServer/0/query"),
      "aMWgrQW0yqg9hL0aho4PVhQ..MwkU7WU82T0PjMsLYsUmzRfB533Q4wZZ7H9__SedqIsDuWZXEUh-IJspC1ZE941yfdeH7zaJqjNM5sAGcIYYG6U82195bWxp5_3mW_UfUsTVD3f3X3xl2nW2LGROL3HS8yFB27uhTxnjRXeS9sA4SHmdEGEEZ7N0",
      QueryParameters(
        outFields = Some(Seq("GEOID", "NAME"))
      )
    ).build(
      initialRequest()
    ).send(
      DefaultSyncBackend()
    ).body match {
      case Right(value) => println(value)
      case Left(value) => fail(value)
    }
  }
}
