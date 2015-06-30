package org.fenixedu.bennu.core.bootstrap.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Methods annotated with {@link Bootstrap} will be invoked whenever the user submits the bootstrap form.
 * </p>
 * 
 * <p>
 * {@code Bootstrap} methods must be static, and may declare arguments of types annotated with {@link Section}.
 * </p>
 * 
 * <p>
 * When the bootstap form is submitted, the sections requested in the method arguments are populated with the values filled by the
 * user, and the method is invoked within the context of a write transaction. Each {@link Bootstrapper} class may define multiple
 * {@link Bootstrap} methods, and they will all run within the same transaction, meaning that failure of any method will cause the
 * whole process to fail.
 * </p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bootstrap {

}
