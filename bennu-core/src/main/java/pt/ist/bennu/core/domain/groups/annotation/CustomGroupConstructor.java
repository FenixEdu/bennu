package pt.ist.bennu.core.domain.groups.annotation;

import static java.lang.annotation.ElementType.CONSTRUCTOR;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ CONSTRUCTOR })
public @interface CustomGroupConstructor {
}
