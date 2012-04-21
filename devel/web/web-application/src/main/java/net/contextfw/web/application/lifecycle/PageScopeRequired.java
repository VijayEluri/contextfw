package net.contextfw.web.application.lifecycle;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation to inform, whether certain method requires existing page scope
 *  
 * <p>
 *  This is a meta-annotation to inform when method should be called in page scope.
 *  This annotation is usable especially in services that themselves are singleton
 *  scope but may require access to page scope.
 * </p>
 * <p>
 *  Currently this annotation is informative only. 
 * </p>
 * @author marko
 *
 */
@Target( { METHOD })
@Retention(RUNTIME)
public @interface PageScopeRequired {

}
