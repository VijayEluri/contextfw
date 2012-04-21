package net.contextfw.web.commons.async.internal.comet;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.PageHandle;
import net.contextfw.web.commons.async.internal.InternalAsyncService;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

import com.google.inject.Singleton;

@Singleton
public class JettyAsyncServlet extends HttpServlet implements CometService {

    private static final long serialVersionUID = 1L;

    private static final BatonProvider BATONS = new BatonProvider();
    
    private transient InternalAsyncService asyncService;
    
    private transient Map<PageHandle, Continuation> continuations =
            Collections.synchronizedMap(new HashMap<PageHandle, Continuation>());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        asyncService.setCurrenHost(req.getLocalAddr() + ":" + req.getLocalPort());
        PageHandle handle = new PageHandle(req.getParameter("handle"));
        synchronized(BATONS.get(handle)) {
            Continuation continuation = ContinuationSupport.getContinuation(req);
            if (asyncService.updateAsync(handle, req, resp, continuation.isResumed())) {
                clearOld(continuations.remove(handle));
            } else {
                asyncService.registerListener(handle);
                clearOld(continuations.put(handle, continuation));
                continuation.suspend();
            }
        }
    }
    
    private void clearOld(Continuation old) {
        if (old != null) {
            try {
                old.complete();
            }
            catch (IllegalStateException e) {
                // Ignored
            }
        }
    }
        

    public JettyAsyncServlet() {
    }
    
    @Override
    public void resume(PageHandle pageHandle) {
        synchronized (BATONS.get(pageHandle)) {
            Continuation continuation = continuations.remove(pageHandle);
            if (continuation != null) {
                continuation.resume();
            }
        }
    }

    @Override
    public void setAsyncService(InternalAsyncService asyncService) {
        this.asyncService = asyncService;
    }
}
