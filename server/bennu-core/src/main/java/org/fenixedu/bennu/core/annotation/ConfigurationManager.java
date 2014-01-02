package org.fenixedu.bennu.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.fenixedu.bennu.core.util.ConfigurationInvocationHandler;

/**
 * Class that holds {@link ConfigurationProperty} annotated methods mapped to the corresponding property in the
 * configuration.property file.
 * 
 * @see {@link ConfigurationInvocationHandler#getConfiguration(Class)}
 * 
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ConfigurationManager {
    String description() default "";
}
