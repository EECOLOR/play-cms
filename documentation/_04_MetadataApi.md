**This documentation is generated from `documentation._04_MetadataApi`**

---
# The metadata API
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

For all requests we are using `/api/metadata` as a prefix.

This is the `messages` file we are using for human friendly messages:
```
article.title.required=The field `Title` can not be empty
article.publishDate.invalidDate=The value {0} is not a valid date


```
Note that this API only supports GET requests, it will return a failure
for any other method
```scala
val methods = Seq("HEAD", "POST", "PUT", "DELETE", "TRACE", "OPTIONS", "CONNECT", "PATCH")
methods.foreach { method =>
  val (status, body) = routeRequest(FakeRequest(method, "/api/metadata"))
  body is obj(
    "status" -> 405,
    "error" -> "methodNotAllowed"
  )
  status is 405
}
success
```
The metadata API is a read only API, metadata can only be changed at
compile time. This is by design, the compiler does a lot of complicated
validation that would otherwise be tricky to implement. On top of that,
it's nice to see the metadata evolve alongside with your code. Modern 
version control is a great help in tracking and managing changes. 

You can get the metadata for all documents like this:
```scala
val (status, body) = GET from "/documents"

status is 200
body is arr(
  obj(
  "id" -> "article",
  "properties" -> arr(
    obj(
      "id" -> "label",
      "name" -> "title"
    ),
    obj(
      "id" -> "label",
      "name" -> "secret",
      "optional" -> true,
      "confidential" -> true
    ),
    obj(
      "id" -> "rich_content",
      "name" -> "body",
      "optional" -> true,
      "extra" -> obj(
        "allowedElements" -> arr(
          "strong", "em", "ul", "ol", "li", "span[class|lang]",
          "a[href|hreflang|title|target]", "br", "p[class|lang]")
      )
    ),
    obj(
      "id" -> "tag",
      "name" -> "tags",
      "set" -> true,
      "nonEmpty" -> false,
      "extra" -> obj(
        "pattern" -> "[a-zA-Z0-9_-]+"
      )
    ),
    obj(
      "id" -> "date",
      "name" -> "date",
      "generated" -> true
    ),
    obj(
      "id" -> "date",
      "name" -> "publishDate",
      "optional" -> true
    )
  )
)
)
```
It's also possible to retrieve the metadata for a single document
```scala
val (status, body) = GET from "/documents/article"

status is 200
body is obj(
  "id" -> "article",
  "properties" -> arr(
    obj(
      "id" -> "label",
      "name" -> "title"
    ),
    obj(
      "id" -> "label",
      "name" -> "secret",
      "optional" -> true,
      "confidential" -> true
    ),
    obj(
      "id" -> "rich_content",
      "name" -> "body",
      "optional" -> true,
      "extra" -> obj(
        "allowedElements" -> arr(
          "strong", "em", "ul", "ol", "li", "span[class|lang]",
          "a[href|hreflang|title|target]", "br", "p[class|lang]")
      )
    ),
    obj(
      "id" -> "tag",
      "name" -> "tags",
      "set" -> true,
      "nonEmpty" -> false,
      "extra" -> obj(
        "pattern" -> "[a-zA-Z0-9_-]+"
      )
    ),
    obj(
      "id" -> "date",
      "name" -> "date",
      "generated" -> true
    ),
    obj(
      "id" -> "date",
      "name" -> "publishDate",
      "optional" -> true
    )
  )
)
```
