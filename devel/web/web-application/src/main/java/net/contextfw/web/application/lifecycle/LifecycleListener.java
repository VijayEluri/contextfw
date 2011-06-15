package net.contextfw.web.application.lifecycle;

/**
 * This interface defines means to follow page lifecyle.
 * 
 * <p>
 *  This listener is meant to be used to follow and to react page lifecycle. Using lifecycle listener 
 *  is especially handy when possible database connections needs
 *  to be opened and closed during the lifecycle of a request
 * </p>
 * 
 * <p>
 *  Can be set by <code>LIFECYCLE_LISTENER<code> by Properties
 * </p>
 * 
 * @see net.contextfw.web.application.properties.Properties.LIFECYCLE_LISTENER
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
     * 
     * 
     * <p>
     *  This method is useful in two ways. First it can be used to initialize possible
     *  database connections etc.
     * </p>
     * <p>
     *  By default this method should return <code>true</code>, but it can be used to 
     *  cancel the client update request by returning <code>false</code>. This can be used
     *  for instance to restrict client from making requests after credentials have been
     *  expired.
     * </p>
     * <p>
     *  In case of <code>false</code> (and also when <code>true</code>) this method can be used to
     *  call methods on components and create updates.
     * </p>
     * <p>
     *  Note that even if <code>false</code> is returned the flow will continue. Only the 
     *  client update request is bypassed.
     * </p>
     * 
     * @return <code>true</code> if client update can continue, <code>false</code> otherwise
     */
    boolean beforeUpdate();
    
    void afterUpdate();
    
    void onException(Exception e);
    
    void onRefresh(String handle);
    
    void onRemove(String handle);
    
    void beforeRender();
    
    void afterRender();
}
