**This documentation is generated from `documentation._08_01_Property_DSL`**

---
#Property DSL

The property DSL consists of multiple traits and abstract classes 
that help discoverability of options.
##`org.qirx.cms.metadata.dsl.Property`

This is a class that can be used as a base for properties, it simplifies 
the implementation of the `validate` and `toJson` methods. It also mixes 
in the `org.qirx.cms.metadata.dsl.PropertyValidation` trait which supplies utility methods
that help with validation.
```scala
object MyProperty extends Property("testProperty") {
  def validate(messages: Messages, value: JsValue): Option[JsObject] = None
  def extraJson: Option[JsObject] = Some(obj("my" -> "property"))
}
```
The `validate` method now reports an error when no value is given
```scala
validate(None) is Some(
  obj(
    "id" -> "testProperty",
    "messageKey" -> "required",
    "message" -> "The property is required"
  )
)
```
The `toJson` method adds the extra json
```scala
val result = MyProperty.toJson
result is obj(
  "id" -> "testProperty",
  "extra" -> obj("my" -> "property")
)
```
##`org.qirx.cms.metadata.dsl.PropertyValidation`


> Pending: TODO

##`?` (optional)

The `org.qirx.cms.metadata.dsl.Property` class provides a `?` by default. Calling it will wrap 
the current property into an `org.qirx.cms.metadata.dsl.OptionalValueProperty`.
```scala
val optionalTestProperty: OptionalValueProperty[TestProperty.type] = TestProperty.?
```
The `org.qirx.cms.metadata.dsl.OptionalValueProperty` class will implement the validation method
so that it will not report anything if the value is not given.
It will also add extra information to the `toJson` method
```scala
val result = optionalTestProperty.toJson
result is obj(
  "id" -> "testProperty",
  "optional" -> true
)
```
##`generated`

The `generated` method is provided by the `org.qirx.cms.metadata.dsl.GeneratableValue` trait. When 
you mix this trait with your property it requires you to specify a method that 
generates the value. Calling it will wrap the current property into a 
`org.qirx.cms.metadata.dsl.GeneratedValueProperty`.
```scala
object Prop extends TestProperty with GeneratableValue {
  def generate = JsString("something")
}
val generatedProperty: GeneratedValueProperty[Prop.type] = Prop.generated
```
This will provide a generator that will generate the property.
Note that the provided generator will also validate the generated property,
a failure during validation results in a runtime error. This is done to keep
the API simple. This means you must always create a test that tests the 
generator you provide for custom properties.
If the value is given, it will not validate
```scala
validate(Some(obj())) is Some(
  obj(
    "id" -> "testProperty",
    "error" -> "generated"
  )
)
```
It will also add extra information to the `toJson` method
```scala
val result = generatedProperty.toJson
result is obj(
  "id" -> "testProperty",
  "generated" -> true
)
```
##`*` (zore or more)

The `*` method is provided by the `org.qirx.cms.metadata.dsl.Identifiable` trait. It effectively 
turns your property into an array. When you mix this trait with your property 
it requires you to specify a method that determines the ideneity of the value.
Calling it will wrap the current property into a `org.qirx.cms.metadata.dsl.ValueSetProperty`.
```scala
object Prop extends Property("testProperty") with Identifiable {
  def validate(messages: Messages, value: JsValue): Option[JsObject] =
    toType[String](value)
      .right.map {
        case "something" => None
        case _ => Some(obj("validation" -> "error"))
      }
      .left.map(Option.apply)
      .merge
  val extraJson = None
  def determineIdentityOf(value: JsValue) = value.as[String]
}

val setProperty: ValueSetProperty[Prop.type] = Prop.*
```
Validation will not fail if no value is given.
Validation will fail when the value is not an array
```scala
validate(Some(obj())) is Some(
  obj(
    "id" -> "testProperty",
    "error" -> "invalidType"
  )
)
```
Validation will fail if any of the provided values do not pass
the property's validation
```scala
validate(Some(arr("something", "nothing", "other"))) is Some(
  obj(
    "id" -> "testProperty",
    "errors" -> obj(
      "1" -> obj("validation" -> "error"),
      "2" -> obj("validation" -> "error")
    )
  )
)
```
Validation will fail if any of the provided values has the same identity
```scala
validate(Some(arr("something", "something"))) is Some(
  obj(
    "id" -> "testProperty",
    "errors" -> obj(
      "1" -> obj(
        "message" -> "The value `something` is present more than once",
        "messageKey" -> "duplicateValue"
      )
    )
  )
)
```
It will also add extra information to the `toJson` method
```scala
val result = setProperty.toJson
result is obj(
  "id" -> "testProperty",
  "set" -> true,
  "nonEmpty" -> false
)
```
##`Confidential`

The `Confidential` method marks the property as confidential. This means 
it will only be accessabile using the private API. This is wrapped around 
the property instead of added as a method because it's not dependend on the 
type of property.
```scala
val prop = Confidential(TestProperty)
```
As a counter example: `generated` can not be called on an optional property.
It's `confidential` property is set to true
It will also add extra information to the `toJson` method
```scala
val result = prop.toJson
result is obj(
  "id" -> "testProperty",
  "confidential" -> true
)
```
