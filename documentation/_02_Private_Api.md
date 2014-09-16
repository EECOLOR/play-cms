**This documentation is generated from `documentation._02_Private_Api`**

---
# The private API
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
Note that this API only supports a few request methods, it will return 
a failure for any non-suported method
```scala
val nonSupportedMethods = Seq("HEAD", "TRACE", "OPTIONS", "CONNECT")
nonSupportedMethods.foreach { method =>
  val (status, body) = routeRequest(FakeRequest(method, "/api/private"))
  body is obj(
    "status" -> 405,
    "error" -> "methodNotAllowed"
  )
  status is 405
}
success
```
The first call we make to the api returns no documents
```scala
val (status, body) = GET from "/article"
status is 200
body is arr()
```
So we need to create a new instance
```scala
val article = obj(
  "title" -> "Article 1",
  "secret" -> "Secret information about Article 1",
  "body" -> arr(
    obj(
      "element" -> "p",
      "children" -> arr(
        "This ",
        obj(
          "element" -> "em",
          "text" -> "article"
        ),
        " is about special things"
      )
    ),
    obj(
      "element" -> "p",
      "attributes" -> obj("class" -> "quote"),
      "children" -> arr(
        "All articles are special"
      )
    )
  ),
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
The article can now be retrieved, note that it generated the id and date
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
Let's create another article
```scala
val article = obj(
  "title" -> "Article 3",
  "tags" -> arr("tag1")
)

val (status, body) = POST(article) to "/article"

status is 201
body is obj(
  "id" -> "article_3"
)
```
As you may have noticed, I accidentally created an article with 
the wrong title, let's fix that. As you can see in the result 
below, the id has also been changed. More on this later on.
```scala
val article = obj(
  "title" -> "Article 2"
)

val (status, body) = withFixedDateTime {
  PUT(article) at "/article/article_3"
}
status is 200
body is obj(
  "id" -> "article_2"
)
```
Note that the article is overwritten
```scala
val (status, body) = GET from "/article/article_3"
status is 200
body is obj(
  "id" -> "article_2",
  "title" -> "Article 2",
  "date" -> "2011-07-10T20:39:21+02:00"
)
```
We could however partially update the article
```scala
val withTags = obj(
  "tags" -> arr("tag1", "tag3")
)

val (status, body) = withFixedDateTime {
  PATCH("/article/article_2") using withTags
}
status is 204
body is null
```
As you can see the result is tags being added
```scala
val (status, body) = GET from "/article/article_2"
status is 200
body is obj(
  "id" -> "article_2",
  "title" -> "Article 2",
  "tags" -> arr("tag1", "tag3"),
  "date" -> "2011-07-10T20:39:21+02:00"
)

```
"|It's not possible to update the id directly. You might have notived
that the id is derived from the document. So in order to change it, 
we need to update content in the document. With the current metadata 
we should update the `title` field as we have marked it as the 
`idField`

If you would scroll up, you could see at one point in time this 
document had another id. The old id might already be stored somewhere 
so it's important we can still retrieve the new document by it's old 
id.
```scala
val (status, body) = GET from "/article/article_3?fields=id,title"

status is 200
body is obj(
  "id" -> "article_2",
  "title" -> "Article 2"
)
```
Note that the document with the old id has been removed
```scala
val (_, body) = GET from "/article?fields=id"

body is arr(obj("id" -> "article_1"), obj("id" -> "article_2"))
```
Let's remove an article
```scala
val (status, body) = DELETE from "/article/article_2"

status is 204
body is null
```
As you can see, it's removed
```scala
val (_, body) = GET from "/article?fields=id"

body is arr(obj("id" -> "article_1"))
```
Let's add an article with the same title and notice that a new
unique id is generated
```scala
val document = obj("title" -> "Article 1")
val (_, body) = POST(document) to "/article"

body is obj(
  "id" -> "article_1-1"
)
```
The default strategy will keep counting if we insert the same id again
```scala
val document = obj("title" -> "Article 1")
val (_, body) = POST(document) to "/article"

body is obj(
  "id" -> "article_1-2"
)
```
If you patch the document and it has impact on the id, the id will be
updated
```scala
val newTitle = obj("title" -> "Article X")
val (status, body) = PATCH("/article/article_1") using newTitle

status is 200
body is obj(
  "id" -> "article_x"
)
```
It can now be retrieved using it's new id
```scala
val (status, _) = GET from "/article/article_x"
status is 200
```
This same effect happens when you are performing a put
If you work with properties that have generated values, note 
that their generator is called on every POST, PUT and PATCH
request.
> Pending: TODO

It's also possible to remove all documents
```scala
val (status, body) = DELETE from "/article"

status is 204
body is null
```
