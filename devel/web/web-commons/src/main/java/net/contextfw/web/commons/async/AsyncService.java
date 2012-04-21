package net.contextfw.web.commons.async;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.lifecycle.PageScopeRequired;

public interface AsyncService {

    /**
     * Prepares asynchronous runnable and returns runnable that can be run
     * asynchronously.
     * 
     * @param runnable
     * @return
     */
    @PageScopeRequired
    Runnable prepare(AsyncRunnable<? extends Component> runnable);

    /**
     * Refreshes pending updates. Usable in LifecycleListener.
     */
    @PageScopeRequired
    void update();
    
}
