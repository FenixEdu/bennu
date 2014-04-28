package org.fenixedu.bennu.core.bootstrap.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Types annotated with {@link Bootstrapper} are used to declare bootstrap {@link Section}s, and to container {@link Bootstrap}
 * methods.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bootstrapper {

    Class<?>[] sections();

    String name();

    String description() default "";

    Class<?>[] after() default {};

    String bundle();

}
