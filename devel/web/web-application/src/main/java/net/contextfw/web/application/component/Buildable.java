package net.contextfw.web.application.component;


import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines that class can be built into DOM-tree.
 * 
 * @see Attribute
 * @see Element
 * @see DOMBuilder
 * @see CustomBuild
 * @see ScriptElement
 * @see FunctionCall
 * @see ComponentFunctionCall
 * 
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Buildable {

    /**
     * The name of the element, defaults to class simple name
     */
    String name() default "";
    
    /**
     * Defines if this class is wrapped to element by its name
     */
    boolean wrap() default true;
}
