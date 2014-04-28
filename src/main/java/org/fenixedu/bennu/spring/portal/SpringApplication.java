package org.fenixedu.bennu.spring.portal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * Marks the annotated class as a Spring-based {@link Application}
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SpringApplication {

    String path();

    String group();

    String title();

    String description() default PortalHandlerMapping.DELEGATE;

    String hint() default "bennu-spring";

}
