# [CSRF Protection](README.md)


Since version 4.0, Bennu provides CSRF Protection for both JAX-RS and Bennu Spring.

A CSRF attack may force an authenticated user to perform an unwanted request on an application he's logged on. You can read more about CSRF [here](https://www.owasp.org/index.php/Cross-Site_Request_Forgery_%28CSRF%29).

Unfortunately, the implemented protection requires you to make some minor tweaks to you application.

## JAX-RS

The CSRF protection implemented in JAX-RS requires that every POST, PUT or DELETE request **must** include a non-empty X-Requested-With header.

This approach was taken due to the fact that it is fairly simple to implement and use, while providing comprehensive protection against CSRF attacks, as HTML forms cannot send headers, and regular AJAX calls don't work as per the same-origin policy.

### Bypassing CSRF validation on specific endpoints 

If you want a certain JAX-RS endpoint to bypass CSRF validation entirely, you must annotate it with the @SkipCSRF annotation.

**CSRF Validation Bypass**
 ``` java
@POST
@SkipCSRF
public String nonCheckedResource() {
    return "ok";
}
 ```

 ### Adding CSRF support to your application
As JAX-RS endpoints are mostly consumed by Javascript applications, this tutorial will focus exclusively on some popular Javascript frameworks. For any other Framework, refer to the documentation on sending request headers.


#### **jQuery**


If you are using jQuery, you're good to go! jQuery already sends the required header, so no action is necessary.

#### **AngularJS**

Unlike jQuery, AngularJS does not send the required header by default. However, setting it to do so is really easy. Just add the following code to your application's code:

**AngularJS CSRF Setup**

``` java
myApp.config(['$httpProvider',function($httpProvider) {
    $httpProvider.defaults.headers.common = $httpProvider.defaults.headers.common || {};
    $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
}]);
```


**Note**: If you are including the Bennu Toolkit as a dependency, this step is already done for you, and the code above is not necessary.

## Bennu Spring


Due to the nature of typical Spring MVC requests, CSRF protection in Spring works in a slightly different way.

Bennu Spring requires that every POST, PUT or DELETE request present a token in either a Form parameter or a Header. This token is unique per-session, and its value must match the value that was previously generated in another request in the same session.

CSRF tokens can be accessed via the CSRFTokenRepository which provides the instance of CSRFToken for the current request.

### Programmatic Access
The CSRFToken can be programmatically accessed by auto-wiring the CSRFTokenRepositor.

### JSP Access

Accessing the token via the token repository would be cumbersome. As such, Bennu provides a bean, that is available in the global context of every JSP file, and allows easy access to the token itself.

To embed the token in your form, add ${csrf.field()}. This will automatically inject a hidden field with the correct value. The name of the injected field may be accessed using ${csrf.parameterName}, and the token value using ${csrf.token}.

Alternatively, if you need to perform a REST call to a Spring-CSRF-Protected endpoint, you may also submit the CSRF token via a header. To obtain the required header name, you may use ${csrf.headerName}.