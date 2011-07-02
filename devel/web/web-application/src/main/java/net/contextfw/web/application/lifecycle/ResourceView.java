package net.contextfw.web.application.lifecycle;

/**
 * <p>
 *  Defines that page view should respond with resource.
 * </p>
 * 
 * <p>
 *  If a view component is implementing this interface, it is considered
 *  being returning resources rather than normal web page. Resource can  
 *  be anything from JSON to plain text-files or images.
 * </p>
 * 
 * <p>
 *  Normally page scope regarding to this view is immediately expired, becuse
 *  it has no purpose. However, using ResourceBody-annotation the expiration can
 *  be disabled. Is needed if system is used in embedded mode.
 * </p>
 * 
 * @see ResourceBody
 */
public interface ResourceView {

    /**
     * Send a response to the client.
     * 
     * <p>
     *  This method must return the response that is sent to web client. 
     *  There are two possibilities. If return values is a subclass of
     *  {@link net.contextfw.web.application.remote.ResourceResponse}
     *  the actual response is served from it.
     * </p>
     * <p>
     *  Otherwise return value is considered to be JSON and is automatically processed
     *  and sent.
     * </p>
     * @return
     *    JSON or <code>ResourceResponse</code>
     */
    Object getResponse();
}
