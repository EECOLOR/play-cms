**This documentation is generated from `documentation._04_01_Get_Failures`**

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

For all requests we are using `/api/metadata` as a prefix.

This is the `messages` file we are using for human friendly messages:
```
article.title.required=The field `Title` can not be empty
article.publishDate.invalidDate=The value {0} is not a valid date


```
Non exsistent endpoint
```scala
val (status, body) = GET from "/non_existing"

status is 404
body is obj(
  "status" -> 404,
  "error" -> "notFound"
)
```
Non existent doucment type
Wrong path
```scala
val (status, body) = GET from "/documents/article/non_existing"

status is 404
body is obj(
  "status" -> 404,
  "error" -> "notFound"
)
```
Authentication failure (using the following method authenticate method)
```scala
  request: RequestHeader => Future.successful(false)
```
```scala
val (status, body) = GET from "/anything"

status is 403
body is obj(
  "status" -> 403,
  "error" -> "forbidden"
)
```
