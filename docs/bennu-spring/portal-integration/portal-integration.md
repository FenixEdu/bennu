# [Portal Integration](./portal-integration.md)


Bennu Spring implements a fully-fledged Bennu Portal Backend.

## Applications
Applications are defined via the @SpringApplication annotation. This annotation has the @Component stereotype, meaning that it is automatically scanned by Spring, provided it is on a package specified in the module's basePackage property.

Spring Applications contain the following properties:

| **Name** | **Requiered** | **Description** |
|:-:|:-:|:-:|
| path | true | The semantic URL fragment provided by this application. |
| group | true | The expression for this application's access group. |
| title | true | The MessageSource key for this application's title. |
| description | true | The MessageSource key for this application's title. If not specified, defaults to the application's title. |
| hint | true | The hint for this application. This serves only to group up applications in Bennu Admin. |


``` java
@SpringApplication(path = "my-app", group = "logged", title = "my.app.title")
public class MyApp {
    (...)
}
```

## Functionalities

In this backend, Functionalities are provided by Spring MVC Controllers, via the @SpringFunctionality annotation.

Spring Functionalities contain the following properties:

| **Name** | **Type** | **Requiered** | **Description** |
|:-:|:-:|:-:|-|
| app | java.lang.Class | true | The application this functionality belongs to. This class **must** be annotated with @SpringApplication. |
| title | java.lang.String | true | The MessageSource key for this functionality's title. |
| description | java.lang.String | false | The MessageSource key for this functionality's description. If not specified, defaults to the functionality's title. |
| accessGroup | java.lang.String | false | The expression for this functionality's access group. Defaults to the application's access group. |


If you want to split your functionality among multiple controllers, you may use the @BennuSpringController annotation. This will bind the annotated controller with the specified functionality type.

Bennu Spring Controllers contain the following property:

| **Name** | **Type** | **Requiered** | **Description** |
|:-:|:-:|:-:|-|
| value | java.lang.Class | true | The functionality this controller is associated with. |