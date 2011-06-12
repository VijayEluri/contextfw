package net.contextfw.web.application.lifecycle;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines a method in a <code>Component</code> that is called after building phase.
 * 
 * <p>
 *  When the DOM-representation of a component has been built, methods that has been annotated
 *  with this annotation are called. It enables component do cleaning up if necessary.
 * </p>
 * 
 * <p>
 *  The annotated method must not take any arguments. Possible return values are discarded.
 * </p>
 * 
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface AfterBuild {
}