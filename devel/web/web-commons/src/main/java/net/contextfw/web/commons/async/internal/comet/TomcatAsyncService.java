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

import com.google.inject.Singleton;

@Singleton
public class TomcatAsyncService implements CometService {

    public static TomcatAsyncService instance;
    
//    private static final String SPACER = "             ";
    
    private final ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(10);

    private final Map<PageHandle, CometEvent> _connections = new HashMap<PageHandle, CometEvent>();
    
    private static final BatonProvider BATONS = new BatonProvider();

    private InternalAsyncService asyncService;
    
    public TomcatAsyncService() {
        instance = this;
    }
    
    public void event(CometEvent event) throws IOException, ServletException {
        HttpServletRequest req = event.getHttpServletRequest();
        PageHandle handle = new PageHandle(req.getParameter("handle"));
        synchronized(BATONS.get(handle)) {
            //String ts = req.getParameter("ts");
            asyncService.setCurrenHost(req.getLocalAddr() + ":" + req.getLocalPort());
            //logEvent(handle, ts, event);
            if (event.getEventType() == EventType.BEGIN) {
                prepareBegin(handle, event);
                if (asyncService.updateAsync(handle, event.getHttpServletRequest(), 
                        event.getHttpServletResponse(), false)) {
                    //log(handle, ts, "\tImmediate response");
                    close(event);
                } else {
                    //log(handle, ts, "\tWaiting...");
                    addConnection(handle, event);
                    asyncService.registerListener(handle);
                }
            } else {
                //log(handle, ts, "\tClose and remove");
                closeAndRemove(handle, event);
            }
        }
    }
    
    @Override
    public void resume(final PageHandle handle) {
        //Tracker.debug("Resuming...");
        pool.execute(new Runnable() {
            public void run() {
                synchronized(BATONS.get(handle)) {
                    try {
                        CometEvent event = remove(handle);
                        if (event != null) {
                            //String ts = event.getHttpServletRequest().getParameter("ts");
                            //log(handle, "\tSending resumed. ts="+ts);
                            asyncService.updateAsync(handle,
                                    event.getHttpServletRequest(),
                                    event.getHttpServletResponse(), true);
                            //log(handle, "Resume sent:" + resumeSent);
                        }
                        close(event);
                    } catch (Throwable e) {
                        //log(handle, "\tResume error");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void setAsyncService(InternalAsyncService asyncService) {
        this.asyncService = asyncService;
    }
    
    private synchronized void prepareBegin(PageHandle handle, CometEvent event) throws UnsupportedOperationException, IOException, ServletException {
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
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
//    private void logEvent(PageHandle handle, String ts, CometEvent event) {
//        log(handle, ts, event.getEventType() +"\t" +event.getEventSubType());
//    }
//    
//    private void log(PageHandle handle, String ts, String msg) {
//        //System.out.println("Comet: " + handle + " " + ts + "\t" + msg);
//    }
//    
//    private void log(PageHandle handle, String msg) {
//        //System.out.println("Comet: " + handle + " " + SPACER + "\t" + msg);
//    }
    
    private synchronized CometEvent remove(PageHandle handle) {
        return _connections.remove(handle);
    }
    
    private void close(CometEvent event) {
        if (event != null) {
            try {
                event.getHttpServletResponse().getWriter().flush();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            try {
                event.close();
            } catch (Throwable e) {
               // e.printStackTrace();
            }
        }
    }
}
