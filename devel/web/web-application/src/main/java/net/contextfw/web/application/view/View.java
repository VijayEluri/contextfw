package net.contextfw.web.application.view;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.contextfw.web.application.component.Component;

@Target( { TYPE })
@Retention(RUNTIME)
public @interface View {
    String[] url() default "";
    String[] property() default "";
    Class<? extends Component> parent() default Component.class;
}