package net.contextfw.web.commons.async.internal.comet;

import net.contextfw.web.application.PageHandle;
import net.contextfw.web.commons.async.internal.InternalAsyncService;

public interface CometService {
    
    void resume(PageHandle handle);
    
    void setAsyncService(InternalAsyncService asyncService);
}
