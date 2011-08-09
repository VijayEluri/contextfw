package net.contextfw.web.application.lifecycle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This interface defines a filter that is used to control request
 * flow. 
 * 
 * <p>
 *  Page flow filter should be seen as a static guardian and throttling facility. 
 * </p>
 * 
 */
public interface PageFlowFilter {

    /**
     * Called by framework before page is about to be created and processed
     * 
     * <p>
     *  It is guaranteed that nothing is written to response before this
     *  method is called. It gives an opportunity to use delay mechanism such
     *  Jetty Continuations to delay request.
     * </p>
     * 
     * <p>
     *  If method returns <code>false</code> request is not processed any further
     *  thus request can be rejected or delayed.
     * </p>
     * 
     * @param scopeCount
     *   Statistics of current active scope count
     * @param request
     *   Current request
     * @param response
     *   Current response
     * @return
     *   <code>true</code> if page initialization can continue.
     */
    boolean beforePageCreate(int scopeCount, 
                             HttpServletRequest request,
                             HttpServletResponse response);
    
    
    /**
     * Called by framework before page is about to be updated
     * 
     * <p>
     *  It is guaranteed that nothing is written to response before this
     *  method is called. It gives an opportunity to use delay mechanism such
     *  Jetty Continuations to delay request.
     * </p>
     * 
     * <p>
     *  If method returns <code>false</code> request is not processed any further
     *  thus request can be rejected or delayed.
     * </p>
     * 
     * @param scopeCount
     *   Statistics of current active scope count
     * @param request
     *   Current request
     * @param response
     *   Current response
     * @return
     *   <code>true</code> if page initialization can continue.
     */
    boolean beforePageUpdate(int scopeCount, 
                             HttpServletRequest request,
                             HttpServletResponse response);
    
    /**
     * Called by framework just after page has been created but not processed
     * 
     * @param scopeCount
     *    Statistics of current active scope count
     * @param remoteAddr
     *    The remote address of call
     * @param handle
     *    The web application handle
     */
    void onPageCreate(int scopeCount, String remoteAddr, String handle);
    
    /**
     * Called by framework just before page update
     * 
     * @param scopeCount
     *    Statistics of current active scope count
     * @param remoteAddr
     *    The remote address of call
     * @param handle
     *    The web application handle
     */
    void onPageUpdate(int scopeCount, String remoteAddr, String handle, int updateCount);
    
    /**
     * Called by framework when page has been removed.
     * 
     * @param scopeCount
     *    Statistics of current active scope count
     * @param remoteAddr
     *    The remote address of call
     * @param handle
     *    The web application handle
     */
    void pageRemoved(int scopeCount, String remoteAddr, String handle);
    
    /**
     * Called by framework just when page expires.
     * 
     * <p>
     *  This method is called when page has been in system so long 
     *  that it has expired and is removed by the framework. Note that
     *  this method is called from scheduled thread.
     * </p>
     * 
     * @param scopeCount
     *    Statistics of current active scope count
     * @param remoteAddr
     *    The remote address of call
     * @param handle
     *    The web application handle
     */
    void pageExpired(int scopeCount, String remoteAddr, String handle);
    
    /**
     * Returns remote address for the request.
     * 
     * <p>
     *  Remote address from request is used to bind each page 
     *  to certain IP address and prevent misuse. In normal circumstances
     *  IP address is the <code>request.getRemoteAddr()</code>. But
     *  if this web application is behind a proxy, application may
     *  not get correct address. In such cases HTTP header <code>X-Forwarded-For</code>
     *  may be useful.
     * </p>
     * 
     * 
     * @param request
     * @return
     */
    String getRemoteAddr(HttpServletRequest request);
}
