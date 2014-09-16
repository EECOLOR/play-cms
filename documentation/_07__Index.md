**This documentation is generated from `documentation._07__Index`**

---
#The Index
The index is used by the Cms to index documents. In practice it's
a transformation from `Index` elements to a Scala `Future`.

More information about this type of transformation can be found at the 
`Store` section.

To create an index simply implement the `transform` method.
```scala
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
```
An in memory version is provide as `org.qirx.cms.testing.MemoryIndex`
An Elastic Search version is provide as `org.qirx.cms.elasticsearch.Index`

For detailed information see [07 01 ElasticSearch](_07_01_ElasticSearch.md)
To create a custom index implementation you can use the supplied `IndexTester`,
it will check if the index behaves as expected.

Note that this will not check your implementation of `Search` as this 
is completely dependent on your index implementation.

The index tester allows for typeclasses to be attached to the failures.
By default the `TypeclassMagnet.None` is attached, which does not 
contain any information.
```scala
val indexTester = new IndexTester

val testResults = indexTester.test(customIndex)

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
```
To use a different typeclass, simply pass it to the tester

Note that when you do this, calling `test` requires you to 
supply the appropriate typeclass instances.

The tests of the built-in stores make use of this feature
```scala
trait CustomTypeclass[T]
object CustomTypeclass {
  implicit val forBoolean: CustomTypeclass[Boolean] = null
  implicit val forJsObjectOption: CustomTypeclass[Option[JsObject]] = null
  implicit val forJsObjectSeq: CustomTypeclass[Seq[JsObject]] = null
  implicit def forJsObjectSeqMap: CustomTypeclass[Map[String, Seq[JsObject]]] = null
  implicit def forJsObjectOptionMap: CustomTypeclass[Map[String, Option[JsObject]]] = null
  implicit val forBooleanSeq: CustomTypeclass[Seq[Boolean]] = null
}

val storeTester = new IndexTester[CustomTypeclass]

val result: Seq[(String, TestResult[CustomTypeclass])] =
  storeTester.test(customIndex)

success
```
