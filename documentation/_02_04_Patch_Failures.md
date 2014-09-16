**This documentation is generated from `documentation._02_04_Patch_Failures`**

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
For these examples we make sure an article exists
```scala
val (status, body) = POST(obj("title" -> "Article 1")) at "/article"
status is 201
body is obj(
  "id" -> "article_1"
)
```
Invalid id
```scala
val (status, body) = PATCH("/article/not_existent") using obj()

status is 404
body is obj(
  "status" -> 404,
  "error" -> "notFound"
)
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

val (status, body) = PATCH("/article/article_1") using article

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
Malformed json
```scala
val (status, body) = PATCH("/article/article_1") using "no json" 

status is 400
body is obj(
  "status" -> 400,
  "error" -> "badRequest"
)
```
Non exsistent endpoint
```scala
val (status, body) = PATCH("/non_existing/article_1") using obj()

status is 404
body is obj(
  "status" -> 404,
  "error" -> "notFound"
)
```
Non exsistent document
```scala
val (status, body) = PATCH("/article/non_existing") using obj() 

status is 404
body is obj(
  "status" -> 404,
  "error" -> "notFound"
)
```
Wrong document json
```scala
val (status, body) = PATCH("/article/article_1") using arr() 

status is 422
body is obj(
  "status" -> 422,
  "error" -> "jsonObjectExpected"
)
```
Wrong path
```scala
val (status, body) = PATCH("/article/article_1/non_existing") using obj()

status is 404
body is obj(
  "status" -> 404,
  "error" -> "notFound"
)
```
