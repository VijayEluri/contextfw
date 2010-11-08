package net.contextfw.web.application.initializer;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.contextfw.web.application.component.Component;

@Target( { TYPE })
@Retention(RUNTIME)
public @interface Initializer {
    String url() default "";
    String urlMatcher() default ""; 
    Class<? extends Component> parent() default Component.class;
}