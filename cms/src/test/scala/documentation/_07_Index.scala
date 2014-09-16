package documentation

import scala.concurrent.Future

import org.qirx.cms.construction.Index
import org.qirx.cms.machinery.~>
import org.qirx.cms.testing.MemoryIndex
import org.qirx.littlespec.Specification

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsObject
import testUtils.codeString
import testUtils.cmsName

class _07_Index extends Specification {

  "#The Index" - {

    val customIndexCode = codeString {
      object CustomIndex extends (Index ~> Future) {
        import Index._
        def transform[x] = {
          case List(metaId, fieldSet) => ???
          case Get(metaId, id, fieldSet) => ???
          case Exists(metaId, id) => ???
          case Put(metaId, id, document) => ???
          case AddId(metaId, id, newId) => ???
          case Delete(metaId, id) => ???
          case DeleteAll(metaId) => ???
          case Search(request, remainingPath) => ???
          case Count(request, remainingPath) => ???
        }
      }
    }

    s"""|The index is used by the $cmsName to index documents. In practice it's
        |a transformation from `Index` elements to a Scala `Future`.
        |
        |More information about this type of transformation can be found at the 
        |`Store` section.
        |
        |To create an index simply implement the `transform` method.
        |```scala
        |$customIndexCode
        |```""".stripMargin - {

      s"An in memory version is provide as `${classOf[MemoryIndex].getName}`" - {
        implicitly[MemoryIndex <:< (Index ~> Future)]
        success
      }

      """|An Elasticsearch version is provide as as a separate library.
         |
         |Tools to help you create your own index implementation can be 
         |found in the testing library""".stripMargin - success

    }
  }
}