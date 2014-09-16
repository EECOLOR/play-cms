package documentation

import org.qirx.littlespec.Specification
import org.qirx.cms.testing.StoreTester
import org.qirx.cms.testing.MemoryStore
import play.api.libs.json.JsObject
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.qirx.cms.testing.TestFailure
import org.qirx.cms.testing.TypeclassMagnet
import org.qirx.cms.testing.TestResult

class _01_Store_Tester extends Specification {

  val customStore = new MemoryStore

  """|To create a custom store implementation you can use the supplied `StoreTester`,
         |it will check if the store behaves as expected.
         |
         |Some test frameworks work better when they have extra information about 
         |the type. Most frameworks make use of typeclasses to help with this. 
         |
         |The store tester allows for typeclasses to be attached to the failures.
         |By default the `TypeclassMagnet.None` is attached, which does not 
         |contain any information.""".stripMargin -
    example {
      val storeTester = new StoreTester

      val testResults = storeTester.test(customStore)

      testResults.foreach {
        case (description, result) =>
          result.fold(
            onSuccess = {
              // report success using your favorite test framework
            },
            onFailure = {
              case failure @ TestFailure(value, expectedValue) =>
                // use the typeclass to get more information about the type
                val none: TypeclassMagnet.None[_] = failure.typeclass
              // report failure using your favorite test framework
            }
          )
      }

      success
    }

  """|To use a different typeclass, simply pass it to the tester
         |
         |Note that when you do this, calling `test` requires you to 
         |supply the appropriate typeclass instances.
         |
         |The tests of the built-in stores make use of this feature""".stripMargin -
    example {
      trait CustomTypeclass[T]
      object CustomTypeclass {
        implicit val forBoolean: CustomTypeclass[Boolean] = null
        implicit val forJsObjectOption: CustomTypeclass[Option[JsObject]] = null
        implicit val forJsObjectSeq: CustomTypeclass[Seq[JsObject]] = null
        implicit def forJsObjectSeqMap: CustomTypeclass[Map[String, Seq[JsObject]]] = null
        implicit def forJsObjectOptionMap: CustomTypeclass[Map[String, Option[JsObject]]] = null
        implicit val forBooleanSeq: CustomTypeclass[Seq[Boolean]] = null
      }

      val storeTester = new StoreTester[CustomTypeclass]

      val result: Seq[(String, TestResult[CustomTypeclass])] =
        storeTester.test(customStore)

      success
    }
}