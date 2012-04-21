package net.contextfw.web.commons.async.internal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.PageHandle;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.component.ComponentRegister;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.lifecycle.PageScopedExecutor;
import net.contextfw.web.application.lifecycle.UpdateExecutor;
import net.contextfw.web.application.scope.WebApplicationStorage;
import net.contextfw.web.commons.async.AsyncRunnable;
import net.contextfw.web.commons.async.BaseAsyncServiceImpl;
import net.contextfw.web.commons.async.internal.comet.CometService;
import net.contextfw.web.commons.async.internal.websocket.WebSocketService;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class AsyncServiceImpl extends BaseAsyncServiceImpl implements InternalAsyncService {

    private final ScheduledThreadPoolExecutor pool;
    private final PageScopedExecutor executor;
    private final Provider<ComponentRegister> register;
    private final Provider<PageHandle> pageHandle;
    private final UpdateExecutor updateExecutor;
    private String host = "";
    
    private final WebApplicationStorage storage;
    private final CometService cometService;
    private final WebSocketService webSocketServiceImpl;
    
    @Inject
    public AsyncServiceImpl(Configuration conf,
                            WebApplicationStorage storage,
                            WebSocketService webSocketServiceImpl,
                            CometService cometService,
                            PageScopedExecutor executor,
                            Provider<ComponentRegister> register,
                            Provider<PageHandle> pageHandle,
                            UpdateExecutor updateExecutor) {
        super(storage, register);
        this.storage = storage;
        this.cometService = cometService;
        this.executor = executor;
        this.register = register;
        this.pageHandle = pageHandle;
        this.updateExecutor = updateExecutor;
        this.webSocketServiceImpl = webSocketServiceImpl;
        cometService.setAsyncService(this);
        //net.contextfw.web.application.util.Tracker.initialized(this);
        pool = new ScheduledThreadPoolExecutor(5);
    }
    
    @Override
    public Runnable prepare(final AsyncRunnable<? extends Component> runnable) {
        final PageHandle handle = pageHandle.get();
        // In page scope
        AsyncServerInfo info = loadInfo(handle);
        info.setUpdating(runnable.getComponentId(), true);
        storeInfo(pageHandle.get(), info);
        
        runnable.postInit(handle, executor, register, this);
        return new Runnable() { public void run() {
            try {
                runnable.run();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } finally {
                // Outside page scope
                storage.executeSynchronized(handle, new Runnable() { public void run() {
                    AsyncServerInfo info = loadInfo(handle);
                    info.setUpdating(runnable.getComponentId(), false);
                    storeInfo(pageHandle.get(), info);
                }});
            }
        }};
    }
    
    @Override
    public void registerListener(final PageHandle handle) {
        storage.executeSynchronized(handle, new Runnable() { public void run() {
            AsyncServerInfo info = loadInfo(handle);
            info.setHost(handle, host);
            storeInfo(handle, info);
        }});
    }

    @Override
    public void requestRefresh(final PageHandle handle, final String componentId) {
        if (!webSocketServiceImpl.asyncUpdate(handle, componentId)) {
            storage.executeSynchronized(handle, new Runnable() { public void run() {
                AsyncServerInfo info = loadInfo(handle);
                info.addRefreshRequest(handle, componentId);
                final String infoHost = info.getHost(handle);
                if (host.equals(infoHost)) {
                    info.removeHost(handle);
                    cometService.resume(handle);
                } else if(infoHost != null) {
                    pool.execute(new Runnable() { public void run() {
                        try {
                            String urlName = "http://"+infoHost+"/asyncRefresh?handle="+handle;
                            new URL(urlName).openConnection().getInputStream().close();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }    
                    }});
                }
                storeInfo(handle, info);
            }});
        }
    }
    
    public boolean updateAsync(final PageHandle handle, 
                               HttpServletRequest request, 
                               HttpServletResponse response,
                               final boolean forced) {
        
        Callable<Boolean> call = new Callable<Boolean>() { public Boolean call() throws Exception {
            return refreshComponents(handle) || forced;
        }};
    
        return updateExecutor.update(handle, call, request, response);
    }

    public void update() {
        refreshComponents(pageHandle.get());
    }
    
    public boolean isExecuting(String componentId) {
        return loadInfo(pageHandle.get()).isUpdating(componentId);
    }

    public void setCurrenHost(String host) {
        this.host = host;
    }

    public void requestRemoteRefresh(final PageHandle handle) {
        storage.executeSynchronized(handle, new Runnable() { public void run() {
            AsyncServerInfo info = loadInfo(handle);
            String infoHost = info.getHost(handle);
            if (host.equals(infoHost)) {
                info.removeHost(handle);
                storeInfo(handle, info);
                cometService.resume(handle);
            }
        }});
    }
}
