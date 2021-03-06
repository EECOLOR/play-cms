**This documentation is generated from `documentation._06_Store`**

---
#The Store
The store is used by the Cms to store documents. In practice it's
a transformation from `Store` elements to a Scala `Future`.

Since both `Store` and `Future` have a single type parameter, we call 
this transformation a natural transformation. Where a function has a
type signature `A => B`, natural transformation has a type signature 
`A[x] => B[x]`.

We have introduced an operator for natural transformations, this allows
us to write the store signature like this: `Store ~> Future`.

`Store[T]` is a sealed trait where `T` is the type of the expected result. 
The `Get` case extends `Store[Option[JsObject]]` meaning it should 
return an `Option` of `JsObject`.

To create a store simply implement the `transform` method.
```scala
object CustomStore extends (Store ~> Future) {
  import Store._
  def transform[x] = {
    case List(metaId, fieldSet) => ???
    case Get(metaId, id, fieldSet) => ???
    case Save(metaId, id, document) => ???
    case AddId(metaId, id, newId) => ???
    case Delete(metaId, id) => ???
    case DeleteAll(metaId) => ???
    case Exists(metaId, id) => ???
  }
}
```
An in memory version is provide as `org.qirx.cms.testing.MemoryStore`
An Elasticsearch version is provide as as a separate library.

Tools to help you create your own store implementation can be 
found in the testing library
