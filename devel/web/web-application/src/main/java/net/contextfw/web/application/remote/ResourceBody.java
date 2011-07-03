package net.contextfw.web.application.remote;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes that the return value of this method is returned to web client.
 * 
 * <p>
 *  When this annotation is used in conjunction with <code>@Remoted</code> annotation
 *  the page flow changes in such way that instead of building a response
 *  the return value of the method is used as response.
 * </p>
 * 
 * <p>
 *  For return values there are two possibilities. If return value implements
 *  <code>ResourceResponse</code>, sending the response is delegated to it. In
 *  other cases the return value is returned as JSON.
 * </p>
 * 
 * <p>
 *  This annotation can also be used with ResourceView-interface. If method getResponse()
 *  and the ResourceBody.expire() is set to <code>false</code>. Page scope will not be
 *  expired immediately.
 * </p>
 *
 * @see Remoted
 * @see ResourceResponse
 * @see net.contextfw.web.application.lifecycle.ResourceView
 *
 */
@Target( { METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceBody {
    
    /**
     * <p>
     *  Defines if page scope should be expired immediately after
     *  response. 
     * </p>
     * <p>
     *  If page is responding in case where application
     *  is to be embedded in another page, then this value must be <code>false</code>.
     * </p>
     * <p>
     *  This value has no effect when using as component resource.
     * </p>
     */
    public boolean expire() default false;
}
