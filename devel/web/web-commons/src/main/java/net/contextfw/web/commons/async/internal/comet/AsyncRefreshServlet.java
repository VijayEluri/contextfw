package net.contextfw.web.commons.async.internal.comet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.PageHandle;
import net.contextfw.web.commons.async.internal.InternalAsyncService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AsyncRefreshServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(AsyncRefreshServlet.class);
    
    private static final long serialVersionUID = 1L;
    
    private InternalAsyncService asyncService = null;

    public AsyncRefreshServlet() {}
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        PageHandle handle = new PageHandle(req.getParameter("handle"));
        LOG.debug("Received async refresh: {}", handle);
        asyncService.requestRemoteRefresh(handle);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Inject
    public void setAsyncService(InternalAsyncService asyncService) {
        this.asyncService = asyncService;
    }
}
