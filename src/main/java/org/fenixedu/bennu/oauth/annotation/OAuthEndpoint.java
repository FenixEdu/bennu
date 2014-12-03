package org.fenixedu.bennu.oauth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * 
 * Specify OAuth2 protected endpoint.
 * 
 * {@link #value()} defines the necessary scopes to invoke the endpoint
 * if {@link #serviceOnly()} is true only service applications can invoke the endpoint (the scopes are ignored)
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OAuthEndpoint {

    public String[] value() default {};

    public boolean serviceOnly() default false;

}
