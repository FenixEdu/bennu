package pt.ist.bennu.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import pt.ist.bennu.core.util.ConfigurationInvocationHandler;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface ConfigurationProperty {
    String key();

    String description() default "";

    String defaultValue() default ConfigurationInvocationHandler.NULL_DEFAULT;
}
