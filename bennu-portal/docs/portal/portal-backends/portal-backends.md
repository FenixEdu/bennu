## [Portal Backends](portal-backends.md)

Bennu Portal supports routing and consistent a consistent application design across multiple presentation technologies. To make this integration possible, each different presentation technology is required to provide a PortalBackend, which will perform the actual processing required to render a functionality, independently of it being invoked directly or via a semantic URL.

Each MenuFunctionality is required to specify the backend that is able to handle it, whenever the functionality's semantic URL is requested.

Portal Backends are required to provide the following:

+ A SemanticURLHandler, which is invoked whenever a functionality for the specific backend is requested. This handler is responsible for invoking the underlying presentation technology.
+ Whether this backend requires server-side layouting, i.e., whether the response body should be intercepted and replaced by the configured theme.
+ The backend key. This key must be unique across all registered backends. It is advised to use the backend's Artifact ID as the backend's name to avoid clashes between modules.
 

By making Portal's routing algorithm independent of the presentation technology and using Backends, Portal can be used as a generic router. FenixEdu Pages is one such example that leverages this routing algorithm.

## Provided Backends
Bennu Portal ships with several out-of-the-box backends to provide common functionality needed by many applications.

### Client-Side
This backend is aimed at providing support for client-side modules (such as Bennu Admin or Bennu Scheduler UI). Due to their nature, these modules do not require any special server-side interaction, they only required layouting and semantic routing.

In this backend, modules provide a description of their own functionality via a apps.json file (if you are using Maven or Gradle, this file should be located in src/main/resources).

The JSON file has the following format:

**apps.json example**
```javascript
{"apps": [{
    /* The Application's title, description and access group */
    "title": { "en-GB" : "Hello World", "pt-PT": "Olá Mundo" },
    "description": { "pt-PT": "O meu primeiro módulo Bennu", "en-GB": "My First Bennu Module" },
    "accessExpression": "anyone",
  
    /* The application's desired semantic URL fragment */
    "path": "hello-world",
  
    /* The functionalities provided by this application */
    "functionalities": [{
        "title" :  {"en-GB" : "Hello World", "pt-PT": "Olá Mundo"},
        "description" : {"en-GB" : "Greets people warmly", "pt-PT": "Saúda pessoas afectivamente"},
        "accessExpression": "anyone",
        /* The functionality's path, typically a hash fragment */
        "path": "#"
    }]
}]}
```

This backend provides server-side layout injection, so that only the content itself is client-side.

**Note**: Due to the way the Servlet API handles hash fragments, it is not possible for the semantic URL engine to forward to the proper hash fragment, and no fragment will be provided. This means that your application must be able to handle URLs without a fragment, or provide a default one.

### Forwarder
The purpose of this backend is to create a generic way to use Portal's functionality tree to create semantic URLs, by simply forwarding any request to the path defined in the functionality's key. This process does not trigger server-side layout injection, and clears the selected functionality before forwarding (giving the target URL a chance to select a more proper functionality).

**Note**: Due to the way the functionality key is used, key uniqueness is not guaranteed in this backend.

### Redirect
This backend is similar to the Forwarder backend, only instead of forwarding the request, the user is redirected (using a temporary redirect) to the desired URL. Just like the Forwarder backend, key uniqueness is not guaranteed.

## Addon backends
Some Bennu Addons (such as Bennu Struts and Bennu Spring) provide their own backends, integrating both Struts 1 and Spring with Bennu Portal. Refer to each project's documentation for additional information.

+ [Bennu Spring](./../../bennu-spring/portal-integration/portal-integration.md)