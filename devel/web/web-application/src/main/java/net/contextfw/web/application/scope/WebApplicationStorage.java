package net.contextfw.web.application.scope;

import net.contextfw.web.application.PageHandle;
import net.contextfw.web.application.WebApplication;

public interface WebApplicationStorage {
    
    void initialize(WebApplication application,
                    String remoteAddr,
                    long validThrough,
                    ScopedWebApplicationExecution execution);

    void update(PageHandle handle,
                String remoteAddr,
                long validThrough,
                ScopedWebApplicationExecution execution);
    
    void execute(PageHandle handle,
                 ScopedWebApplicationExecution execution);

    /**
     * Runs runnable in locked environment.
     * 
     * This method does not initialized page scope at all, it only locks
     * the scope execution so that no other thread or server can access the scope.
     * 
     * It is useful to use loadLarge and storeLarge in this mode.
     * 
     * @param handle
     * @param runnable
     */
    void executeSynchronized(PageHandle handle, Runnable runnable);
    
    void refresh(PageHandle handle, 
                 String remoteAddr,
                 long validThrough);

    void remove(PageHandle handle,
                String remoteAddr);
    
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
    void storeLarge(PageHandle handle, String key, Object obj);
    
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
    <T> T loadLarge(PageHandle handle, String key, Class<T> type);
    
}
