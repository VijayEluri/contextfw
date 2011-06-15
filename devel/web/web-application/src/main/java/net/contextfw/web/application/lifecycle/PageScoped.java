package net.contextfw.web.application.lifecycle;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.ScopeAnnotation;

/**
 * Denotes that there must exist only on instance of this class in one page
 * 
 * <p>
 *  To make this annotation usable, the class must be injected by Guice.
 *  By making page components or other helper classes page scoped they
 *  are easily accessible from different classes and still be 
 *  certain that the instance is always the same. 
 * </p>
 */
@Target( { TYPE })
@Retention(RUNTIME)
@ScopeAnnotation
public @interface PageScoped {
}