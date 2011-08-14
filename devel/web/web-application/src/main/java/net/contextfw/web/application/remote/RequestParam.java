package net.contextfw.web.application.remote;

import static java.lang.annotation.ElementType.FIELD;
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
@Target(FIELD)
@Retention(RUNTIME)
public @interface RequestParam {
    /**
     * The name of the path parameter
     */
    String name() default "";
    /**
     * The resolution when mapping parameter to required type fails. 
     */
    ErrorResolution onError() default ErrorResolution.SEND_NOT_FOUND_ERROR;
    /**
     * The resolution when parameter is null
     */
    ErrorResolution onNull() default ErrorResolution.SET_TO_NULL;
}