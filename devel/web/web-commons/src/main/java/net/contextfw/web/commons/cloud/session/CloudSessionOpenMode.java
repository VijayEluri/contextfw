package net.contextfw.web.commons.cloud.session;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 * A convinience annotation for controlling open mode for sessions
 * 
 * <p>
 *  This annotation is supported by CloudSessionLifecycleListener.
 * </p>
 * 
 * @author marko.lavikainen@netkoti.fi
 * @see CloudSessionLifecycleListener
 */
@Target( { METHOD })
@Retention(RUNTIME)
@BindingAnnotation 
public @interface CloudSessionOpenMode {
    
    OpenMode value();
}
