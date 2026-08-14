package io.github.dejarol.arcgis.spark.connector.core.http

import io.github.dejarol.arcgis.spark.connector.core.BasicSpec
import org.scalatest.Inspectors
import sttp.client4.{BasicBody, BasicBodyPart, StringBody}
import sttp.model.Part

import scala.reflect.ClassTag

/**
 * TODO
 */
trait SttpRequestBuilderSpec
  extends BasicSpec
    with Inspectors {

  /**
   * TODO
   * @param body
   * @param assertion
   * @tparam B
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
   * TODO
   * @param actual
   * @param expected
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
