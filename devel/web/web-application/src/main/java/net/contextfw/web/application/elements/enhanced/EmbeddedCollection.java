package net.contextfw.web.application.elements.enhanced;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target( { FIELD, METHOD })
@Retention(RUNTIME)
public @interface EmbeddedCollection {
    String name() default "";
    String elementName();
    BuildPhase phase() default BuildPhase.BOTH;
    String[] updateModes() default {};
    
    @SuppressWarnings("unchecked")
    Class<? extends ElementBuilder> elementBuilder() default DefaultElementBuilder.class;
}
