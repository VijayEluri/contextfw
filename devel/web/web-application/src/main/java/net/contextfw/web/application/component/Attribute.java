package net.contextfw.web.application.component;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Denotes that a class property or return value of a method is to be added as element attribute 
 * into DOM-tree during build phase.
 * 
 * <p>
 *  If property or return value is <code>null</code> then attribute is not added to the tree.
 * </p>
 */
@Target( { FIELD, METHOD })
@Retention(RUNTIME)
public @interface Attribute {
    
    /**
     * The name of the attribute.
     * 
     * <p>
     *  Defaults to the property or method name
     * </p>
     */
    String name() default "";
    
    /**
     * Defines if this attribute should be built in to DOM-tree during component creation  
     */
    boolean onCreate() default true;
    
    /**
     * Defines if this attribute should be built in to DOM-tree during component update  
     */
    boolean onUpdate() default true;
    
}