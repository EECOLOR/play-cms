**This documentation is generated from `documentation._08_02_Label`**

---
#`org.qirx.cms.metadata.properties.Label`

The `org.qirx.cms.metadata.properties.Label` class is intended for simple strings.
It has (by default) the id `label`
It will not validate when no json is given
```scala
validate(None) is Some(
  obj(
    "id" -> "label",
    "messageKey" -> "required",
    "message" -> "The property is required"
  )
)
```
It will not validate if the type of property is not a string
```scala
validate(Some(obj())) is Some(
  obj(
    "id" -> "label",
    "error" -> "invalidType"
  )
)
```
It will not validate if the string is empty
```scala
validate(Some(JsString(""))) is Some(
  obj(
    "id" -> "label",
    "messageKey" -> "empty",
    "message" -> "The property may not be empty"
  )
)
```
It will validate for non empty strings
Allows the use of another id
```scala
object CustomLabel extends Label("custom_label")
```
