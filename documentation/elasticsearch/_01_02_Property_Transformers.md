**This documentation is generated from `documentation._01_02_Property_Transformers`**

---
For the built-in types a transformers are provided.

These are the default transformers
Any type that has no explicit transformer will not be transformed
`OptionalValueProperty[Custom]` is transformed using the `Custom` transformer
`GeneratedValueProperty[Custom]` is transformed using the `Custom` transformer
`ValueSetProperty[Custom]` is transformed using the `Custom` transformer
`RichContent` transforms
```json
{
  "[name]" : [ "one ", {
    "text" : "two"
  } ]
}
```
to
```json
{
  "[name]" : [ "one ", {
    "text" : "two"
  } ],
  "[name]_text" : "one two"
}
```
