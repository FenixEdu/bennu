## [Users and Security (Prior to Bennu 4)](./users-and-security.md)

**Deprecation Notice**

The information in this page only applies to versions prior to Bennu 4. You can find the updated information [here](./../users-and-security/users-and-security.md).


Bennu provides user accounts and authentication. The User accounts are supported in Bennu only at the domain level. To manage accounts take a look at the [bennu-user-management](https://github.com/FenixEdu/bennu-user-management) project.

Besides the authentication mechanism itself Bennu ensures that at any point in the source code the user in session is accessible statically through:

```C
org.fenixedu.bennu.core.security.Authenticate.getUser()
```
or from the HttpSession using the property:
```C
Authenticate.LOGGED_USER_ATTRIBUTE
```
## Authentication Mechanisms
There are two supported authentication mechanisms: [CAS](http://www.jasig.org/cas) and password based authentication.

CAS authentication can be enabled by setting the properties in the configuration.properties| file: cas.enabled=true and cas.serverUrl to your cas server installation url.


The password authentication mechanism is enabled when CAS isn't. So just set cas.enabled=false. Passwords are stored in the database, salted and hashed. No interfaces for password recovery or change are provided by the framework, to use this authentication mechanism you must provide them yourself.

## Authentication Listeners
Bennu provides a way of registering event listeners over any successful login. To do so you must implement:

```C
org.fenixedu.bennu.core.security.AuthenticationListener
```

and register it in the start-up of the application using:
```C
Authenticate.addAuthenticationListener(AuthenticationListener)
```


 