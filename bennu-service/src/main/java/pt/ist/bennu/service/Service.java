package pt.ist.bennu.service;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * This will mark methods as services. It will guarantee that it will be
 * executed within a write transaction, and that the whole write transaction is
 * atomic.
 * 
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ METHOD })
public @interface Service {
}
