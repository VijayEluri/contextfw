package net.contextfw.web.application.component;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Denotes that a class property or return value of a method is to be added as child element 
 * into DOM-tree during build phase.
 * 
 * <p>
 *  If property or return value is <code>null</code> then element is not added to the tree.
 * </p>
 */
@Target( { FIELD, METHOD })
@Retention(RUNTIME)
public @interface Element {
    
    /**
     * The name of the element.
     * 
     * <p>
     *  Defaults to the property or method name
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
     * Defines if class property containing <code>Component</code> should be automatically registered
     * to rendering tree.
     * 
     * <p>
     *  If class property contains <code>Component</code> after it has been injected by Guice and
     *  this property is <code>true</code> it is automatically registered to rendering tree. In normal
     *  situations this is the wanted behavior. If property is <code>null</code> then it is ignored. 
     * </p>
     * 
     * <p>
     *  <b>Note!</b> This is effective only when instance is initialized by Guice and the instance of the
     *  containing class extends <code>Component</code>. 
     * </p> 
     */
    boolean autoRegister() default true;
    
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
