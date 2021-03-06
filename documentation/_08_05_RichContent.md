**This documentation is generated from `documentation._08_05_RichContent`**

---
#`org.qirx.cms.metadata.properties.RichContent`

The `org.qirx.cms.metadata.properties.RichContent` class is intended for rich html content.
It has (by default) the id `rich_content`
It will not validate when no json is given
```scala
validate(None) is Some(
  obj(
    "id" -> "rich_content",
    "messageKey" -> "required",
    "message" -> "The property is required"
  )

)
```
It will not validate if the type of property is not an array
It will validate when the array is empty
It will not validate in the case of: 
- An invalid structure
- An element that is not allowed
- An attribute that is not allowed
```scala
val result = validate(Some(
  arr(
    obj("element" -> "strong"),
    obj("element" -> "h1"),
    arr("incorrect element"),
    obj("incorrect" -> "element"),
    obj(
      "element" -> "strong",
      "attributes" -> obj("not_class" -> "value", "not" -> "")
    )
  )
))

result is Some(
  obj(
    "id" -> "rich_content",
    "errors" -> arr(
      obj(
        "messageKey" -> "elementNotAllowed",
        "message" -> "The element `h1` is not allowed"
      ),
      obj(
        "messageKey" -> "invalidElement",
        "message" -> "The element `[\"incorrect element\"]` is invalid"
      ),
      obj(
        "messageKey" -> "invalidElement",
        "message" -> "The element `{\"incorrect\":\"element\"}` is invalid"
      ),
      obj(
        "messageKey" -> "attributesNotAllowed",
        "message" -> "The attribute(s) `not_class` and `not` is/are not allowed"
      )
    )
  )
)
```
It will validate when contents are correct
Allows the use of other allowed elements
```scala
object CustomRichContent extends RichContent("custom_rich_content", Seq(
  RichContentElement("p", Seq("class")),
  RichContentElement("div", Seq("class")),
  RichContentElement("span", Seq("class"))
))
```
Provides the allowed elements in it's `toJson` method
```scala
RichContent.toJson is obj(
  "id" -> "rich_content",
  "extra" -> obj(
    "allowedElements" -> arr(
      "strong", "em", "ul", "ol", "li", "span[class|lang]",
      "a[href|hreflang|title|target]", "br", "p[class|lang]"
    )
  )
)
```
