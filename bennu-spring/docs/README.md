# [Bennu Spring](README.md)

**Bundling**

```
Since Bennu 4, Bennu Spring is part of the main Bennu distribution, and thus shares the same version.
```

Bennu Spring provides integration between Spring Web MVC 4 and the Bennu Framework. It also implements a Portal Backend, supporting Spring Controllers as Portal Functionalities.



## Extension
Bennu Spring takes care of all the initialization of the Spring Framework, using annotation-based configuration. To allow modules to provide their own configuration, a special annotation type is provided, @BennuSpringModule, which must be applied to a class in the package org.fenixedu.bennu (or sub-package). This class will be picked up by Spring, and can specify further configuration.

**BennuSpringModule**

``` java
package org.fenixedu.bennu;
 
import org.fenixedu.bennu.spring.BennuSpringModule;
 
@BennuSpringModule(basePackages = { "org.fenixedu.core.service", "org.fenixedu.academic.ui" }, bundles = "FenixEduAcademicResources")
public class FenixEduAcademicSpringConfiguration {
}
```

The basePackages property allows extending the set of packages scanned by Spring (it has the exact same semantics as the @ComponentScan annotation). The bundles property allows the specification of Resource Bundles to be used for Spring's MessageSource. The specified bundle must be located at src/main/webapp/WEB-INF/resources/<bundle><lang>.properties.

**Note**: Usually a configuration class (annotated with @BennuSpringModule) is required per-module, as it is the only way for Spring to know where your module's classes are.


## Converters

When developing modules for the FenixEdu family, you will often find yourself needing to interact with some FenixEdu-specific types. As such, Bennu Spring provides close integration with those types, by allowing Spring MVC Controllers to specify Domain Objects and LocalizedStrings as method arguments.

This means that the following method signature is possible out-of-the-box:

``` java
@RequestMapping("/{site}")
public String updateSiteDescription(@PathVariable Site site, @RequestParam LocalizedString description)
```

## Exception Handling

When a DomainException is thrown in the body of a Spring MVC Controller method, Bennu Spring will catch it, and send a response with the status code defined in the exception. 

