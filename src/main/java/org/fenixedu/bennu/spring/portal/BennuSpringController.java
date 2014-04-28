package org.fenixedu.bennu.spring.portal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.fenixedu.bennu.portal.model.Functionality;
import org.springframework.stereotype.Controller;

/**
 * Marks the annotated controller as being part of a {@link Functionality}.
 * 
 * This annotation can be used in place of {@link Controller}.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
@Controller
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BennuSpringController {

    Class<?> value();

}
