package net.contextfw.web.application.lifecycle;

/**
 * This interface defines means to follow page lifecyle.
 * 
 * <p>
 *  This listener is meant to be used to follow and to react page lifecycle. Using lifecycle listener 
 *  is especially handy when possible database connections needs
 *  to be opened and closed during the lifecycle of a request.
 * </p>
 * 
 * <p>
 *  Can be set by <code>LIFECYCLE_LISTENER</code> by Configuration
 * </p>
 * 
 * <p>
 *  There is also another class <code>PageFlowFilter</code> that serves 
 *  similar purpose but is meant for gathering statistics and throttling
 *  requests if needed.
 * </p>
 * 
 * @see net.contextfw.web.application.configuration.Configuration.LIFECYCLE_LISTENER
 * @see PageFlowFilter
 */
public interface LifecycleListener {

    /**
     * Called by framework before page initialization begins
     */
    void beforeInitialize();
    
    /**
     * Called by framework after page initialization has ended
     */
    void afterInitialize();
    
    /**
     * <p>
     *  Called by framework before update
     * </p>
     * <p>
     *  This method returns a boolean. When <code>true</code> update can continue and requests
     *  from web client are processed. If <code>false</code> request processing is bypassed.
     * </p>
     * <p>
     *  Returning <code>false</code> false is useful in cases where request cannot be
     *  accepted for instance if user credentials have expired. This is needed because
     *  update requests cannot easily be prevented with url-based filtering techniques.
     * </p>
     * <p>
     *  In case of <code>false</code> (and also when <code>true</code>) this method can be used to
     *  call methods on components and create updates. This is useful if page should be redirected
     *  or some message component should display something on web page.
     * </p>
     * @return <code>true</code> if client update can continue, <code>false</code> otherwise
     */
    boolean beforeUpdate();

    /**
     * Called by framework after update has finished
     */
    void afterUpdate();
    
    /**
     * Called by framework if any exception is thrown.
     * 
     * <p>
     *  After this call pageflow is compeletely cancelled and no further calls are
     *  made, so it is safe for instance to close database connection here.
     * </p>
     */
    void onException(Exception e);
    
    /**
     * Called before rendering phase
     */
    void beforeRender();
    
    /**
     * Called after rendering phase
     */
    void afterRender();
}
