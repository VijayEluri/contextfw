package net.contextfw.web.application.remote;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 */
@Target({FIELD, METHOD})
@Retention(RUNTIME)
public @interface RequestParam {
    /**
     * The name of the request parameter
     */
    String name() default "";
    /**
     * The resolution when mapping parameter to required type fails. 
     */
    ErrorResolution onError() default ErrorResolution.SEND_NOT_FOUND_ERROR;
    /**
     * The resolution when parameter is null
     */
    ErrorResolution onNull() default ErrorResolution.SET_TO_NULL;
}