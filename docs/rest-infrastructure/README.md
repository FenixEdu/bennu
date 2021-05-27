## [**Rest Infrastructure**](README.md)

Bennu provides an out-of-the-box configuration of  [JAX RS 2](https://jax-rs-spec.java.net/). It creates an [Application](https://jax-rs-spec.java.net/nonav/2.0/apidocs/javax/ws/rs/core/Application.html) class, mapped to /api, meaning that all Resource classes will be mapped as follows: /api/<resource-path>.

To all **Resources** extending **JsonAwareResource** a mechanism for translating objects into json is also provided. Three families of methods are provided:

+ **viewers**: turn objects into json
+ **creators**: turn json into a new object of a given type
+ **updaters**: apply the changes given in json format to a given object instance

These methods rely on a registry system of type translators. The registry is based on annotations: **JsonCreator<T>**, **JsonUpdater<T>**, **JsonViewer<T>**, or simply **JsonAdapter<T>** that extends the other three.

As an example, this is the adapter for the **Group** type:

```java
@DefaultJsonAdapter(Group.class)
public class GroupJsonAdapter implements JsonAdapter<Group> {
    @Override
    public Group create(JsonElement json, JsonBuilder ctx) {
        return Group.parse(json.getAsJsonObject().get("expression").getAsString());
    }
    @Override
    public Group update(JsonElement json, Group obj, JsonBuilder ctx) {
        return Group.parse(json.getAsJsonObject().get("expression").getAsString());
    }
    @Override
    public JsonElement view(Group group, JsonBuilder ctx) {
        JsonObject object = new JsonObject();
        object.addProperty("expression", group.expression());
        object.addProperty("name", group.getPresentationName());
        object.addProperty("accessible", group.isMember(Authenticate.getUser()));
        return object;
    }
}
```
With this defined, in a **JsonAwareResource** one could simply return:

```java
return view(Group.managers()); 
```
And the endpoint in question with return the json format of the **Group**.


The methods in the **JsonAwareResource** can be parameterized to use custom JsonAdapters.

### Parameter Converters
Bennu provides multiple [Parameter Converters](https://jersey.java.net/apidocs/latest/jersey/javax/ws/rs/ext/ParamConverter.html) for commonly used types, which are not supported by JAX RS out of the box:

+ DomainObject subclasses
+ Gson's JsonElement subclasses
