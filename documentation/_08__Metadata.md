**This documentation is generated from `documentation._08__Metadata`**

---
#Metadata

Metadata is at the core of the ${cmsName}, there are different kinds of 
metadata that operate on different levels.
##Document metadata

Document metadata provides information about the document. It has a 
convenient factory that allows easy creation. If you want to supply 
you own document metadata, please look at the source of that class 
to get a suggestion on rolling your own.
```scala

      Document(
        id = "The identifier for this type of document",
        idField = """
          The field that will be used to determine the identifier of
          document instances.
        """)(
          "property x" -> `property metadata`,
          "property y" -> `property metadata`
        )

      success
   
```
##Property metadata

Property metadata provides information about a single property. There 
are different types of properties provided. It is however possible to 
introduce new types. Please look at the source of existing implementations 
to get suggestions of creating your own.

For detailed information see [08 01 Property DSL](_08_01_Property_DSL.md) 

Note that the following structure allows you to reuse metadata under a 
different identifier. This can be useful when you need to supply other 
constructor arguments
```scala

      class CustomProperty(id: String) extends Property(id) {

        def validate(messages: Messages, value: JsValue): Option[JsObject] = ???
        def extraJson: Option[JsObject] = ???
      }

      object CustomProperty extends CustomProperty("customRichContent")
      
      success
   
```
This comes in handy if you are using a property metadata type that accepts arguments
```scala
object CustomRichContent extends RichContent(
  id = "customRichContent",
  allowedElements = Seq(
    RichContentElement("a", Seq("href", "hreflang", "title", "target")),
    RichContentElement("br"), RichContentElement("p", Seq("class", "lang"))
  )
)
success
```
