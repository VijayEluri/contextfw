package net.contextfw.web.application.component;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target( { FIELD, METHOD })
@Retention(RUNTIME)
public @interface Element {
    String name() default "";
    boolean autoRegister() default true;
    boolean onCreate() default true;
    boolean onUpdate() default true;
}
