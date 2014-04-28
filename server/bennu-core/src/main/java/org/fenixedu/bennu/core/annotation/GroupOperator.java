package org.fenixedu.bennu.core.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.fenixedu.bennu.core.groups.CustomGroup;

/**
 * Defines {@link CustomGroup} implementations as part of the group language, giving them a function name.
 * 
 * @author Pedro Santos (pedro.miguel.santos@tecnico.ulisboa.pt)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ TYPE })
public @interface GroupOperator {
    /**
     * Unique name (system wide) for the group. Must be a proper identifier: only alphanumeric characters, no spaces. The
     * {@code _} character is also allowed.
     * 
     * @return
     */
    String value();
}
