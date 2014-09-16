**This documentation is generated from `documentation._01_GettingStarted`**

---
#Getting started

The first thing you need to do is to create an instance of `Cms`  
and provide the information it needs to operate.
```scala
import org.qirx.cms.Cms
import org.qirx.cms.metadata.dsl._
import org.qirx.cms.metadata.properties._
import scala.concurrent.Future
import play.api.mvc.RequestHeader

def customAuthenticate(requestHeader: RequestHeader): Future[Boolean] =
  Future.successful {
    requestHeader.headers
      .get("X-Qirx-Authenticate")
      .filter(_ == "let me in")
      .isDefined
  }

val documents = Seq(
  Document(id = "article", idField = "title")(
  "title" -> Label,
  "secret" -> Confidential(Label.?),
  "body" -> RichContent.?,
  "tags" -> Tag.*,
  "date" -> Date.generated,
  "publishDate" -> Date.?
)
)

val cms = new Cms(
  pathPrefix = "/api",
  authenticate = customAuthenticate,
  environment = new TestEnvironment,
  documents = documents
)
```
The `Cms` has a single method to handle the requests,
this method will automatically select the appropriate action. 
Below an example of it within GlobalSettings.
```scala
import play.api.GlobalSettings
import play.api.mvc.Handler
import play.api.mvc.RequestHeader

object CustomGlobal extends GlobalSettings {

  override def onRouteRequest(request: RequestHeader): Option[Handler] =
    cms.handle(request, orElse = super.onRouteRequest)
}
```
This gives you an API that consists of three parts:
 - *private* - Allows you to manage documents
 - *public* - Allows you to retrieve and search documents
 - *metadata* - Provides the metadata you specified

## The private API
For detailed information see [02 Private Api](_02_Private_Api.md)

This part of the API allows you to change content, that's the 
reason this requires authentication. Note that we have specified 
the authentation mechanism when we created the `Cms`.
```scala
val article = obj("title" -> "Article 1")
val auth = "X-Qirx-Authenticate" -> "let me in"

val (status, body) = withFixedDateTime {
  POST(article) withHeader auth to "/api/private/article"
}

body is obj(
  "id" -> "article_1"
)
status is 201
```
Failing to authicate results in a response like this:
```scala
obj(
  "status" -> 403,
  "error" -> "forbidden"
)          
```
## The public API
For detailed information see [03 Public Api](_03_Public_Api.md)

This part of the API allows you to search, count and retrieve content, 
it does not require authentication.
```scala
val (status, body) = GET from "/api/public/article"

status is 200
body is arr(
  obj(
    "id" -> "article_1",
    "title" -> "Article 1",
    "date" -> "2011-07-10T20:39:21+02:00"
  )
)
```
## The metada API
For detailed information see [04 MetadataApi](_04_MetadataApi.md)

This part of the API allows you to retrieve the metadata of documents,
because it can contain sensitive information it requires 
authentication. If you need it to be publicly available, just do 
some smart stuff in you authenticate method.
```scala
val auth = "X-Qirx-Authenticate" -> "let me in"

val (status, body) = GET withHeader auth from "/api/metadata/documents/article"

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
