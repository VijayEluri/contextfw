package net.contextfw.web.application.component;


import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RUNTIME)
public @interface Buildable {
    /**
     * If falsee the wrapping class is not appended as element
     * @return
     */
    boolean wrap() default true;
    String name() default "";
}
