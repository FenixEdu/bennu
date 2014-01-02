package org.fenixedu.bennu.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.fenixedu.bennu.core.util.ConfigurationInvocationHandler;

/**
 * Binds the annotated method to the property with the <code>key()</code> in the configuration.properties file.
 * 
 * @see {@link ConfigurationManager}
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface ConfigurationProperty {
    String key();

    String description() default "";

    String defaultValue() default ConfigurationInvocationHandler.NULL_DEFAULT;
}
