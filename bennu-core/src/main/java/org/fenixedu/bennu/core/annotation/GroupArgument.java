package org.fenixedu.bennu.core.annotation;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.fenixedu.bennu.core.groups.CustomGroupRegistry;
import org.fenixedu.bennu.core.groups.Group;

/**
 * Define {@link Group} implementation fields as group arguments in the group language.
 * 
 * @author Pedro Santos (pedro.miguel.santos@tecnico.ulisboa.pt)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ FIELD })
public @interface GroupArgument {
    /**
     * The value defines the key of the argument in the language: {@code mygroup(<key>="xpto",<key>="xptu")}. By default is the
     * same as the field name. The special value {@code ""} means no key is needed in the language for that argument, as in:
     * mygroup("xpto", "xptu")
     * 
     * @return the key for the argument in the group language
     */
    String value() default CustomGroupRegistry.ARGUMENT_NAME_AS_FIELD_NAME;
}