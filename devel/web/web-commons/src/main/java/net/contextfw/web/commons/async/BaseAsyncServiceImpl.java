package net.contextfw.web.commons.async;

import java.util.Set;

import net.contextfw.web.application.PageHandle;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.component.ComponentRegister;
import net.contextfw.web.application.scope.WebApplicationStorage;
import net.contextfw.web.commons.async.internal.AsyncServerInfo;

import com.google.inject.Provider;

public abstract class BaseAsyncServiceImpl {

    private final Provider<ComponentRegister> register;
    
    private static final String KEY = "asyncServerInfo";
    
    private final WebApplicationStorage storage;
    
    public BaseAsyncServiceImpl(WebApplicationStorage storage, 
                            Provider<ComponentRegister> register) {
        this.storage = storage;
        this.register = register;
    }
    
    protected AsyncServerInfo loadInfo(PageHandle handle) {
        AsyncServerInfo info = storage.loadLarge(handle, KEY, AsyncServerInfo.class);
        return info == null ? new AsyncServerInfo() : info;
    }
    
    protected void storeInfo(PageHandle handle, AsyncServerInfo info) {
        storage.storeLarge(handle, KEY, info);
    }
    
    protected boolean refreshComponents(PageHandle handle) {
        AsyncServerInfo info = loadInfo(handle);
        Set<String> requests = info.purgeRefreshRequests(handle);
        storeInfo(handle, info);
        if (requests != null) {
            for (String _ : requests) {
                Component component = register.get().findComponent(Component.class, _);
                if (component != null) {
                    component.refresh();
                }
            }
            return true;
        } else {
            return false;
        }        
    }
}
