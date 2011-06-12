package net.contextfw.web.application.component;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Denotes that method is to build DOM-tree in custom manner. 
 * 
 * <p>
 *  If there is a need to build DOM-tree in certain custom manner, where
 *  using statically annotated classes are not suitable, it is possible to get the
 *  raw {@link DOMBuilder} and use it to build DOM-elements dynamically
 * </p>
 * <p>
 *  The method must take exactly one argument which type must be <code>DOMBuilder</code>. 
 *  Possible return value is discarded
 * </p>
 * 
 * @see DOMBuilder
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface CustomBuild {
    /**
     * The name of the child element.
     * 
     * <p>
     *  Defaults to the method name
     * </p>
     */
    String name() default "";
    
    /**
     * Defines if this element should be built in to DOM-tree during component creation  
     */
    boolean onCreate() default true;
    
    /**
     * Defines if this element should be built in to DOM-tree during component update  
     */
    boolean onUpdate() default true;
    
    /**
     * Defines if the child element should be wrapped with a DOM-element containing the name of the property
     * or method.
     * 
     * <p>
     *  If explicit name is given it overrides the actual name.
     * </p>
     * @return
     */
    boolean wrap() default true;
}