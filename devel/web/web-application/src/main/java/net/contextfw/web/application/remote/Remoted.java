package net.contextfw.web.application.remote;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a method that is callable from client side.
 *
 * <p>
 *  The method can take any combination of arguments as long as they
 *  are parseable from primitives or json.
 * </p>
 */
@Target( { METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Remoted {

}
