**This documentation is generated from `documentation._02_02_Post_Failures`**

---
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

For all requests we are using `/api/private` as a prefix.

This is the `messages` file we are using for human friendly messages:
```
article.title.required=The field `Title` can not be empty
article.publishDate.invalidDate=The value {0} is not a valid date


```
Invalid instance
```scala
val article = obj(
  "title" -> 0,
  "body" -> "no json",
  "tags" -> "not an array",
  "date" -> "is generated",
  "publishDate" -> "invalid date"
)

val (status, body) = POST(article) to "/article"

status is 422
body is obj(
  "status" -> 422,
  "propertyErrors" -> arr(
    obj(
      "id" -> "label",
      "name" -> "title",
      "error" -> "invalidType"
    ),
    obj(
      "id" -> "rich_content",
      "name" -> "body",
      "error" -> "invalidType"
    ),
    obj(
      "id" -> "tag",
      "name" -> "tags",
      "error" -> "invalidType"
    ),
    obj(
      "id" -> "date",
      "name" -> "date",
      "error" -> "generated"
    ),
    obj(
      "id" -> "date",
      "name" -> "publishDate",
      "messageKey" -> "invalidDate",
      "message" -> "The value `invalid date` is not a valid date"
    )
  )
)
```
Empty instance
```scala
val (status, body) = POST(obj()) to "/article"

status is 422
body is obj(
  "status" -> 422,
  "propertyErrors" -> arr(
    obj(
      "id" -> "label",
      "name" -> "title",
      "messageKey" -> "required",
      "message" -> "The field `Title` can not be empty"
    )
  )
)
```
Malformed json
```scala
val (status, body) = POST("no json") to "/article"

status is 400
body is obj(
  "status" -> 400,
  "error" -> "badRequest"
)
```
Non exsistent endpoint
```scala
val (status, body) = POST(obj()) to "/non_existing"

status is 404
body is obj(
  "status" -> 404,
  "error" -> "notFound"
)
```
Wrong document json
```scala
val (status, body) = POST(arr()) to "/article"

status is 422
body is obj(
  "status" -> 422,
  "error" -> "jsonObjectExpected"
)
```
Wrong path
```scala
val (status, body) = POST(obj()) to "/article/non_existing"

status is 404
body is obj(
  "status" -> 404,
  "error" -> "notFound"
)
```
