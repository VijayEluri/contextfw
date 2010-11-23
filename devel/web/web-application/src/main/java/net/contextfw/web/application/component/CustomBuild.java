package net.contextfw.web.application.component;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(METHOD)
@Retention(RUNTIME)
public @interface CustomBuild {
    String name() default "";
    boolean onCreate() default true;
    boolean onUpdate() default true;
    boolean onPartialUpdate() default true;
    boolean wrap() default true;
}