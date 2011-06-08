package net.contextfw.web.application.component;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Denotes that a class property or return value of a method is to be added as element attribute 
 * into DOM-tree during build phase
 * 
 * <p>
 *  If property or return value is <code>null</code> then attribute is not added to the tree.
 * </p>
 */
@Target( { FIELD, METHOD })
@Retention(RUNTIME)
public @interface Attribute {
    String name() default "";
    boolean onCreate() default true;
    boolean onUpdate() default true;
    boolean onPartialUpdate() default true;
}