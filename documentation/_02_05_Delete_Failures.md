**This documentation is generated from `documentation._02_05_Delete_Failures`**

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
Invalid id
```scala
val (status, body) = DELETE from "/article/not_existent"

status is 404
body is obj(
  "status" -> 404,
  "error" -> "notFound"
)
```
Non exsistent endpoint
```scala
val (status, body) = DELETE from "/non_existing/article_1"

status is 404
body is obj(
  "status" -> 404,
  "error" -> "notFound"
)
```
Wrong path
```scala
val (status, body) = DELETE from "/article/article_1/non_existing"

status is 404
body is obj(
  "status" -> 404,
  "error" -> "notFound"
)
```
