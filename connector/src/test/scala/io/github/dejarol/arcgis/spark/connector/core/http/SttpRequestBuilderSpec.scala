package io.github.dejarol.arcgis.spark.connector.core.http

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec
import org.scalatest.Inspectors
import sttp.client4.{BasicBody, BasicBodyPart, StringBody}
import sttp.model.Part

import scala.reflect.ClassTag

/**
 * Shared assertions for specs that exercise STTP request builders.
 *
 * @since 0.1.0
 */
trait SttpRequestBuilderSpec
  extends BasicSpec
    with Inspectors {

  /**
   * Asserts that a request body has the expected concrete type, then runs a typed check.
   *
   * @param body      request body under test
   * @param assertion additional assertions on the cast body
   * @tparam B expected concrete body type
   * @since 0.1.0
   */
  protected final def assertBodyIsInstanceOf[B <: BasicBody: ClassTag](
                                                                        body: BasicBody
                                                                      )(
                                                                        assertion: B => Unit
                                                                      ): Unit = {

    body shouldBe a[B]
    assertion(body.asInstanceOf[B])
  }

  /**
   * Asserts that multipart parts match the expected name/value pairs.
   *
   * @param actual   multipart parts present on the request
   * @param expected expected field names mapped to string values
   * @since 0.1.0
   */
  protected final def assertMultiPartsMatch(
                                             actual: Seq[Part[BasicBodyPart]],
                                             expected: Map[String, String]
                                           ): Unit = {

    // [1.1] For each expected part
    forAll(expected.toSeq) {
      case (k, v) =>

        // [1.2] Find the part with the same name
        val maybePart = actual.find {
          _.name.equals(k)
        }

        maybePart shouldBe defined

        // [1.3] Check the part body
        val partBody = maybePart.get.body
        partBody shouldBe a[StringBody]
        partBody.asInstanceOf[StringBody].s shouldBe v
    }
  }
}
