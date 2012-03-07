package net.contextfw.web.application.scope;

import javax.servlet.http.HttpServletRequest;

import net.contextfw.web.application.WebApplication;
import net.contextfw.web.application.WebApplicationHandle;

public interface WebApplicationStorage {
    
    void initialize(WebApplication application,
                    HttpServletRequest request,
                    long validThrough,
                    ScopedWebApplicationExecution execution);

    void update(WebApplicationHandle handle,
                HttpServletRequest request,
                long validThrough,
                ScopedWebApplicationExecution execution);
    
    void execute(WebApplicationHandle handle,
                 ScopedWebApplicationExecution execution);

    void refresh(WebApplicationHandle handle, 
             HttpServletRequest request,
             long validThrough);

    void remove(WebApplicationHandle handle,
            HttpServletRequest request);
    
    /**
     * Stores store large object to page scope.
     * 
     * <p>
     *  In normal circumstances page scope should be kept as small as possible, so that
     *  it does not use memory mor than needed. However, in some occasions large objects
     *  should be stored. This may happen when after uploading document or creating images.
     * </p>
     * <p>
     *  In such cases this method can be used to store object to page scope in such way that it 
     *  may be serialized immediately. It should be noted that DefaultWebApplicationStorage simply keeps these
     *  objects in memory. 
     * </p>
     * <p>
     *  The good side effect with this approach is that resources are cleaned when page scope is
     *  removed.
     * </p>
     * <p>
     *  Storing object does not require active page scope, but is recommended. 
     * </p>
     * <p>
     * @param key
     *  The uniqe key to object
     * @param obj
     *  The object, null value removes the object
     */
    void storeLarge(WebApplicationHandle handle, String key, Object obj);
    
    /**
     * Loads large object from page scope.
     * 
     * <p>
     *  This method loads previously stored large object from page scope.
     * </p>
     * <p>
     *  Loading object does not require active page scope, but is recommended. 
     * </p>
     * @param key
     * @param type
     * @return
     */
    <T> T loadLarge(WebApplicationHandle handle, String key, Class<T> type);
    
}
