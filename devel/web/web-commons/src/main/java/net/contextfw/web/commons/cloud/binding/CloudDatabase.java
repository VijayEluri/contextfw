package net.contextfw.web.commons.cloud.binding;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

@Target( { PARAMETER, FIELD, METHOD })
@Retention(RUNTIME)
@BindingAnnotation 
public @interface CloudDatabase {

}
