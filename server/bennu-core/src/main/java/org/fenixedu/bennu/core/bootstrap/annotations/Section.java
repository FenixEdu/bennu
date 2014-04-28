package org.fenixedu.bennu.core.bootstrap.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A {@link Section} represents a page in the bootstrap UI. Each section may have {@link Field} methods, which will show as a form
 * field to be filled by the user.
 * 
 * Note that this annotation may only be applied to interfaces!
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Section {
    String name();

    String description();

    String bundle();
}
