package net.contextfw.web.commons.async.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.PageHandle;
import net.contextfw.web.application.lifecycle.PageScopeRequired;
import net.contextfw.web.commons.async.AsyncService;

public interface InternalAsyncService extends AsyncService {

    void setCurrenHost(String host);
    
    void registerListener(PageHandle handle);
    
    void requestRefresh(PageHandle handle, String componentId);
    
    void requestRemoteRefresh(PageHandle handle);
    
    boolean updateAsync(final PageHandle handle, 
            HttpServletRequest request, 
            HttpServletResponse response, 
            boolean force);
    
    @PageScopeRequired
    boolean isExecuting(String componentId);
}
