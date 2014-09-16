**This documentation is generated from `documentation._07_Index`**

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
An Elasticsearch version is provide as as a separate library.

Tools to help you create your own index implementation can be 
found in the testing library
