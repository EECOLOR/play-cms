**This documentation is generated from `documentation._01_01_Property_Mappings`**

---
For the built-in types a mappings are provided.

These are the default mappings:
`OptionalValueProperty[Custom]` is delegating to to the `Custom` mapping
`GeneratedValueProperty[Custom]` is delegating to to the `Custom` mapping
`ValueSetProperty[Custom]` is delegating to to the `Custom` mapping
`ConfidentialProperty` is mapped as
```json
{ }
```
`Identifiable` is mapped as
```json
{
  "[name]" : {
    "type" : "string",
    "index" : "not_analyzed"
  }
}
```
`Label` is mapped as
```json
{
  "[name]" : {
    "type" : "string"
  }
}
```
`Date` is mapped as
```json
{
  "[name]" : {
    "type" : "date",
    "format" : "date_time_no_millis"
  }
}
```
`RichContent` is mapped as
```json
{
  "[name]" : {
    "enabled" : false
  },
  "[name]_text" : {
    "type" : "string"
  }
}
```
`Tag` is provided by `Identifiable`
