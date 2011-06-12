package net.contextfw.web.application.remote;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines if remoted method invocation should be delayed.
 * 
 * <p>
 *  It is possible to delay remote method invocation by annotating method with this
 *  annotation. When such method is about to be invoked the component is assigned to
 *  <code>DelayedUpdateHandler</code> which will determine if the call should be delayed. 
 * </p>
 * 
 * @see Remoted
 * @see DelayedUpdateHandler
 */
@Target( { METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Delayed {
    @SuppressWarnings("rawtypes")
	Class<? extends DelayedUpdateHandler> value();
}
