package org.fenixedu.bennu.core.bootstrap.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A {@link Field} method within a {@link Section} will represent a field in the bootstrap form, and its value will be filled by
 * the value provided by the user.
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {
    String name();

    String[] validValues() default {};

    String hint() default "";

    boolean isMandatory() default true;

    FieldType fieldType() default FieldType.TEXT;

    int order();
}
