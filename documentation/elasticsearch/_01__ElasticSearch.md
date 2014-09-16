**This documentation is generated from `documentation._01__ElasticSearch`**

---
#The ElasticSearch index
The Elastic Search index requires access to your document metadata because
it will create mappings based on that metadata when it's instantiated.

The index needs more metadata than is normally available, to make it more 
usable you need to create an instance of `elasticsearch.Document` instead
of the normal `Document`.

This `elasticsearch.Document` requires the properties to have extra index
information. That information can be automatically added when you import 
the correct implicit conversion.
```scala
import org.qirx.cms.elasticsearch
import elasticsearch.index.Implicits._

val documents = Seq(
  elasticsearch.index.Document(id = "article", idField = "title")(
    "title" -> Label,
    "secret" -> Confidential(Label.?),
    "body" -> RichContent.?,
    "tags" -> Tag.*,
    "date" -> Date.generated,
    "publishDate" -> Date.?
  )
)
val endpoint = "http://localhost:9200"
val indexName = "test_example"

val index = new elasticsearch.Index(documents, endpoint, indexName, client)
```
The index has created a mapping using the information in the metadata
```scala
val response = client.url(s"$endpoint/$indexName/_mapping").get.map(_.json)

val result = Await.result(response, 2.seconds)

result is obj(
  "test_example" -> obj(
    "mappings" -> obj(
      "article" -> obj(
        "dynamic" -> "strict",
        "date_detection" -> false,
        "properties" -> obj(
          "id" -> obj(
            "type" -> "string",
            "index" -> "not_analyzed"
          ),
          "title" -> obj(
            "type" -> "string"
          ),
          "body" -> obj(
            "type" -> "object",
            "enabled" -> false
          ),
          "body_text" -> obj(
            "type" -> "string"
          ),
          "tags" -> obj(
            "type" -> "string",
            "index" -> "not_analyzed"
          ),
          "date" -> obj(
            "type" -> "date",
            "format" -> "date_time_no_millis"
          ),
          "publishDate" -> obj(
            "type" -> "date",
            "format" -> "date_time_no_millis"
          )
        )
      )
    )
  )
)
```
Note that the an extra field has been introduced for `body` called 
`body_text`. This field contains the text as a string instead of 
the rich text structure. This can be used to perform searches.

Below an example of a search that would fail if we did not have 
this field. As a bonus it can be used for highlighting as well.
```scala

          val article = obj(
            "title" -> "Test article",
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
              )
            )
          )

          val articleInIndex = index(Index.Put("article", "test_id", article))
          Await.result(articleInIndex, 1.second)

          val queryWithHighlight =
            obj(
              "query" -> obj(
                "match" -> obj(
                  "body_text" -> obj("query" -> "\"this article\"")
                )
              ),
              "highlight" -> obj(
                "fields" -> obj("body_text" -> obj())
              )
            )

          val response =
            client
              .url(s"$endpoint/$indexName/_search")
              .withBody(queryWithHighlight)
              .get

          val result = Await.result(response, 1.second)

          val source = (result.json \\ "_source").head
          source is obj(
            "title" -> "Test article",
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
              )
            ),
            "body_text" -> "This article is about special things"
          )

          val highlight = (result.json \\ "highlight").head
          highlight is obj(
            "body_text" -> arr("<em>This</em> <em>article</em> is about special things")
          )
       
```
Finally we need to close the client
It is possible to provide index information for custom property types
The index provides search handling that will act as a proxy to the 
Elastic Search `_search` endpoint.
Calling `Search` with a request and `Seq("article")` as 
remaining path segments results in the following call to 
Elastic Search:
```scala
calledUrl is s"$endpoint/$indexName/article/_search"
```
Note that the search method will extract the `"hits"` element
from the result.

The result from the server:
```
{"hits":{"total":1,"hits":[{"_id":"some id"}]}}
```
```scala
result is obj(
  "total" -> 1,
  "hits" -> arr(
    obj("_id" -> "some id")
  )
)
```
The index will forward the query string of the incoming request
It will also forward the body
The index provides count handling that will act as a proxy to the 
Elastic Search `_count` endpoint.
Calling `Count` with a request and `Seq("article")` as 
remaining path segments results in the following call to 
Elastic Search:
```scala
calledUrl is s"$endpoint/$indexName/article/_count"
```
The count method will return the `"count"` element from the result.

The result from the server:
```
{"count":1,"_shards":{"some":"value"}}
```
```scala
result is obj(
  "count" -> 1
)
```
The index will forward the query string of the incoming request
It will also forward the body
