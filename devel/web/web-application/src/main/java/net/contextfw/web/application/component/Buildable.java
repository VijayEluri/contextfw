package net.contextfw.web.application.component;


import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RUNTIME)
public @interface Buildable {
    /**
     * If true the wrapping class is not appended as element
     * @return
     */
    boolean noWrapping() default false;
}
