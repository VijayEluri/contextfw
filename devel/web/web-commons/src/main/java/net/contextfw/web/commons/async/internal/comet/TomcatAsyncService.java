package net.contextfw.web.commons.async.internal.comet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.contextfw.web.application.PageHandle;
import net.contextfw.web.commons.async.internal.InternalAsyncService;

import org.apache.catalina.CometEvent;
import org.apache.catalina.CometEvent.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

@Singleton
public class TomcatAsyncService implements CometService {

    private static final Logger LOG = LoggerFactory.getLogger(TomcatAsyncService.class);
    
    private static TomcatAsyncService instance;
    
    private static final String INWID = "I know what I'm doing";
    
    private final ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(10);

    private final Map<PageHandle, CometEvent> _connections = new HashMap<PageHandle, CometEvent>();
    
    private static final BatonProvider BATONS = new BatonProvider();

    private InternalAsyncService asyncService;
    
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
            value="ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification=INWID)
    public TomcatAsyncService() {
        instance = this;
    }
    
    
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
            value="AvoidCatchingException", justification=INWID)
    public void event(CometEvent event) throws IOException, ServletException {
        HttpServletRequest req = event.getHttpServletRequest();
        PageHandle handle = new PageHandle(req.getParameter("handle"));
        synchronized(BATONS.get(handle)) {
            asyncService.setCurrenHost(req.getLocalAddr() + ":" + req.getLocalPort());
            if (event.getEventType() == EventType.BEGIN) {
                prepareBegin(handle, event);
                if (asyncService.updateAsync(handle, event.getHttpServletRequest(), 
                        event.getHttpServletResponse(), false)) {
                    close(event);
                } else {
                    addConnection(handle, event);
                    asyncService.registerListener(handle);
                }
            } else {
                closeAndRemove(handle, event);
            }
        }
    }
    
    @Override
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
            value="AvoidCatchingException", justification=INWID)
    public void resume(final PageHandle handle) {
        pool.execute(new Runnable() {
            public void run() {
                synchronized(BATONS.get(handle)) {
                    try {
                        CometEvent event = remove(handle);
                        if (event != null) {
                            asyncService.updateAsync(handle,
                                    event.getHttpServletRequest(),
                                    event.getHttpServletResponse(), true);
                        }
                        close(event);
                    } catch (Exception e) {
                        LOG.debug("Exception", e);
                    }
                }
            }
        });
    }

    public void setAsyncService(InternalAsyncService asyncService) {
        this.asyncService = asyncService;
    }
    
    private synchronized void prepareBegin(PageHandle handle, CometEvent event) throws IOException, ServletException {
        event.setTimeout(30000);
        CometEvent old = _connections.get(handle);
        if (old != null) {
            closeAndRemove(handle, old);
        }
    }
    
    private synchronized void addConnection(PageHandle handle, CometEvent event) {
        _connections.put(handle, event);
    }
    
    private synchronized void closeAndRemove(PageHandle handle, CometEvent event) {
        _connections.remove(handle);
        close(event);
        try {
            event.close();
        } catch (Exception e) {
            LOG.debug("Exception", e);
        }
    }
    
    private synchronized CometEvent remove(PageHandle handle) {
        return _connections.remove(handle);
    }
    
    private void close(CometEvent event) {
        if (event != null) {
            try {
                event.getHttpServletResponse().getWriter().flush();
            } catch (Exception e) {
                LOG.debug("Exception", e);
            }
            try {
                event.close();
            } catch (Exception e) {
                LOG.debug("Exception", e);
            }
        }
    }
    
    public static TomcatAsyncService getInstance() {
        return instance;
    }
}
