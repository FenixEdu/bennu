package myorg.util;

import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.TYPE;

@Retention(RUNTIME)
@Target( { TYPE })
public @interface ClassNameBundle {

    String bundle() default "";

    String key() default "";
}
