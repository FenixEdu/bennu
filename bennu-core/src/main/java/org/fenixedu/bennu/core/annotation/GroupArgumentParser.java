package org.fenixedu.bennu.core.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.fenixedu.bennu.core.groups.ArgumentParser;

/**
 * Defines a parser for some argument type. Annotated types must implement {@link ArgumentParser}.
 * 
 * @author Pedro Santos (pedro.miguel.santos@tecnico.ulisboa.pt)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ TYPE })
public @interface GroupArgumentParser {
}