package net.contextfw.web.application.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.ScopeAnnotation;

@Target( { TYPE })
@Retention(RUNTIME)
@ScopeAnnotation
public @interface WebApplicationScoped {
}