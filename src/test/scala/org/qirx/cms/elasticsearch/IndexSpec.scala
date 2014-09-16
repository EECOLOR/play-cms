package org.qirx.cms.elasticsearch

import org.qirx.cms.testing.IndexTester
import org.qirx.cms.testing.TestFailure
import org.qirx.littlespec.Specification

import play.api.libs.ws.WS
import play.api.test.Helpers
import testUtils.PrettyPrint
import testUtils.TestApplication.fakeApplication

class IndexSpec extends Specification {

  "The ElasticSearch index should" - Helpers.running(fakeApplication()) {
    import play.api.libs.concurrent.Execution.Implicits.defaultContext
    import play.api.Play.current

    val indexTester = new IndexTester[PrettyPrint]

    val endpoint = "http://localhost:9200"

    val result = indexTester.test(new Index(Seq.empty, endpoint, "test_index", WS.client))

    result.foreach {
      case (description, result) =>
        result.fold(
          onSuccess = createFragment(description, success),
          onFailure = {
            case testFailure @ TestFailure(value, expectedValue) =>
              val prettyPrint = testFailure.typeclass
              val prettyValue = prettyPrint print value
              val prettyExpectedValue = prettyPrint print expectedValue

              val failureDescription =
                s"""|Expected:
                    |$prettyExpectedValue
                    |Got:
                    |$prettyValue""".stripMargin

              createFragment(description, failure(failureDescription))
          }
        )
    }

    success
  }
}