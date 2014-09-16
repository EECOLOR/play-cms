**This documentation is generated from `documentation._03__Public_Api`**

---
# The public API
For the examples in this specification we use the following
`Cms` definition:

```scala
val article = Document(id = "article", idField = "title")(
  "title" -> Label,
  "secret" -> Confidential(Label.?),
  "body" -> RichContent.?,
  "tags" -> Tag.*,
  "date" -> Date.generated,
  "publishDate" -> Date.?
)

new Cms(
  pathPrefix = "/api",
  authenticate = { _ => Future.successful(true) },
  environment = testEnvironment,
  documents = Seq(article)
)
```

For all requests we are using `/api/public` as a prefix.

This is the `messages` file we are using for human friendly messages:
```
article.title.required=The field `Title` can not be empty
article.publishDate.invalidDate=The value {0} is not a valid date


```
This API provides the same GET endpoints that are available in the 
private API without authentication.

The difference here is that the public API runs on the `Index` instead 
of the `Store`.

The first call we make returns no documents
```scala
val (status, body) = GET from "/article"
status is 200
body is arr()
```
Note that this API only supports GET requests, it will return a failure
for any other method
```scala
val methods = Seq("HEAD", "POST", "PUT", "DELETE", "TRACE", "OPTIONS", "CONNECT", "PATCH")
methods.foreach { method =>
  val (status, body) = routeRequest(FakeRequest(method, "/api/public"))
  body is obj(
    "status" -> 405,
    "error" -> "methodNotAllowed"
  )
  status is 405
}
success
```
Before I can show you how the api works I create a new document
```scala
val article = obj(
  "title" -> "Article 1",
  "tags" -> arr("tag1", "tag2")
)

val (status, body) = withFixedDateTime {
  POST(article) to "/article"
}

status is 201
body is obj(
  "id" -> "article_1"
)
```
The article can now be retrieved
There are scenario's where you want to list articles, but you don't
want to retrieve all fields.
```scala
val (status, body) = GET from "/article?fields=id,title"

status is 200
body is arr(
  obj(
    "id" -> "article_1",
    "title" -> "Article 1"
  )
)
```
You can retrieve a single document by specifying it's id
```scala
val (status, body) = GET from "/article/article_1?fields=id,title"

status is 200
body is obj(
  "id" -> "article_1",
  "title" -> "Article 1"
)
```
If a document is updated, this change is reflected in the index as well
```scala
val updateTitle = obj("title" -> "Article 2")
println("patch")
val r = PATCH("/article/article_1") using updateTitle
println(r)

val (status, body) = GET from "/article/article_1?fields=id,title"
status is 200
body is obj(
  "id" -> "article_1",
  "title" -> "Article 2"
)
```
If a documents id is updated, it's can be retrieved by it's new id.
```scala
val newId = obj("id" -> "article_2")
PATCH("/article/article_1") using newId

val (status, body) = GET from "/article/article_2?fields=id,title"
status is 200
body is obj(
  "id" -> "article_2",
  "title" -> "Article 2"
)
```
Note that it's also still available at it's old id
```scala
val (status, body) = GET from "/article/article_1?fields=id,title"
status is 200
body is obj(
  "id" -> "article_2",
  "title" -> "Article 2"
)
```
The actual document with the old id has been removed
```scala
val (status, body) = GET from "/article?fields=id"
status is 200
body is arr(obj("id" -> "article_2"))
```
Because of the public nature of the index, confidential properties 
are not returned.
```scala
val addSecret = obj("secret" -> "A secret about Article 2")

withFixedDateTime {
  PATCH("/article/article_2") using addSecret
}

val (status, body) = GET from "/article/article_2"
status is 200
body is obj(
  "id" -> "article_2",
  "title" -> "Article 2",
  "tags" -> arr("tag1", "tag2"),
  "date" -> "2011-07-10T20:39:21+02:00"
)
```
This is also the case when listing or searching documents.
When a document is deleted, it's also deleted from the index
```scala
val (status, _) = DELETE from "/article/article_2"
status is 204

val (_, body) = GET from "/article"
body is arr()
```
I've added two documents to make sure I can show removal of multiple documents
```scala
POST(obj("title" -> "Article 1")) to "/article"
POST(obj("title" -> "Article 2")) to "/article"

val (_, body) = GET from "/article?fields=id"
body is arr(obj("id" -> "article_1"), obj("id" -> "article_2"))
```
Removing multiple documents
```scala
val (status, _) = DELETE from "/article"
status is 204

val (_, body) = GET from "/article"
body is arr()
```
##Search

The public API has a special endpoint called `search`. This endpoint 
is special in the sense that it is not handled by the `Cms` itself, 
but by the index. This allows you to implement an index that fits your 
needs.
```scala
val (status, body) = GET from "/search/testPath"
status is 200
body is obj(
  "info" -> "Response from test index to search at `testPath`"
)
```
##Count

The public API has a special endpoint called `count`. This endpoint 
is special in the sense that it is not handled by the `Cms` itself, 
but by the index. This allows you to implement an index that fits your 
needs.
```scala
val (status, body) = GET from "/count/testPath"
status is 200
body is obj(
  "info" -> "Response from test index to count at `testPath`"
)
```
