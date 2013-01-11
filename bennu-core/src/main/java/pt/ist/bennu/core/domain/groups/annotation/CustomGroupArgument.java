package pt.ist.bennu.core.domain.groups.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD })
public @interface CustomGroupArgument {
	int index();

	boolean multiple() default false;
}
