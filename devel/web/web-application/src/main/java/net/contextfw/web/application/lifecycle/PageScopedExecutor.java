package net.contextfw.web.application.lifecycle;

import net.contextfw.web.application.PageHandle;

public interface PageScopedExecutor {
    
    void execute(PageHandle handle, Runnable runnable);
}
