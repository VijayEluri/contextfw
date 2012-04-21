package net.contextfw.web.commons.async;

import net.contextfw.web.application.PageHandle;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.component.ComponentRegister;
import net.contextfw.web.application.lifecycle.PageScopedExecutor;
import net.contextfw.web.commons.async.internal.InternalAsyncService;

import org.apache.commons.lang.mutable.MutableObject;

import com.google.inject.Provider;

/**
 * Base class for supporting asynchronous page updates
 *
 * @param <C>
 */
public abstract class AsyncRunnable<C extends Component> {

    private PageHandle handle = null;
    private final String componentId;
    private final Class<C> componentClass;
    private PageScopedExecutor pageScopedExecutor = null;
    private Provider<ComponentRegister> register = null;
    private InternalAsyncService asyncService = null;
    
    @SuppressWarnings("unchecked")
    public AsyncRunnable(C component) {
        componentId = component.getId();
        componentClass = (Class<C>) component.getClass();
    }
 
    @SuppressWarnings("unchecked")
    public <OUT> OUT executeScoped(final Function<C, OUT> func) {
        final MutableObject rv = new MutableObject();
        pageScopedExecutor.execute(handle, new Runnable() {
            public void run() {
                C in = register.get().findComponent(componentClass, getComponentId());
                rv.setValue(func.apply(in));
            }
        });
        return (OUT) rv.getValue();
    }

    /**
     * Internal usage only
     */
    public AsyncRunnable<C> postInit(PageHandle handle, 
                         PageScopedExecutor pageScopedExecutor,
                         Provider<ComponentRegister> register,
                         InternalAsyncService asyncService) {
        this.register = register;
        this.handle = handle;
        this.pageScopedExecutor = pageScopedExecutor;
        this.asyncService = asyncService;
        return this;
    }
    
    public void requestRefresh() {
        asyncService.requestRefresh(handle, getComponentId());
    }

    public String getComponentId() {
        return componentId;
    }
    
    public abstract void run();
}
