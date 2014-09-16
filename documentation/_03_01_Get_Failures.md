**This documentation is generated from `documentation._03_01_Get_Failures`**

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

For all requests we are using `/api/public` as a prefix.

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
Non exsistent endpoint
```scala
val (status, body) = GET from "/non_existing"

status is 404
body is obj(
  "status" -> 404,
  "error" -> "notFound"
)
```
Non existent doucment
Wrong path
```scala
val (status, body) = GET from "/article/article_1/non_existing"

status is 404
body is obj(
  "status" -> 404,
  "error" -> "notFound"
)
```
