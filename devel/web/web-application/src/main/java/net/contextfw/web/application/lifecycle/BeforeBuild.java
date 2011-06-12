package net.contextfw.web.application.lifecycle;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * Defines a method in a <code>Component</code> that is called before building phase.
 * 
 * <p>
 *  When the DOM-representation of a component is about to be built method that has been annotated
 *  with this annotation are called. It enables component do resource allocations if needed.
 * </p>
 * 
 * <p>
 *  The annotated method must not take any arguments. Possible return values are discarded.
 * </p>
 * 
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface BeforeBuild {
}