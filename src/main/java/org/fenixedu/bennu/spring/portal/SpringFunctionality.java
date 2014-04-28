package org.fenixedu.bennu.spring.portal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.fenixedu.bennu.portal.model.Functionality;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Marks the annotated Controller as a Spring-based {@link Functionality}.
 * 
 * This annotation can be used in place of {@link Controller}.
 * 
 * The path for the functionality is inferred from the {@link RequestMapping} of the controller.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
@Controller
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SpringFunctionality {

    Class<?> app();

    String title();

    String description() default PortalHandlerMapping.DELEGATE;

    String accessGroup() default PortalHandlerMapping.DELEGATE;

}
