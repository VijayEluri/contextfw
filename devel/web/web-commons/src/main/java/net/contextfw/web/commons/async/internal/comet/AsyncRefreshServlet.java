package net.contextfw.web.commons.async.internal.comet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.PageHandle;
import net.contextfw.web.commons.async.internal.InternalAsyncService;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AsyncRefreshServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    private InternalAsyncService asyncService = null;

    public AsyncRefreshServlet() {}
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        asyncService.requestRemoteRefresh(new PageHandle(req.getParameter("handle")));
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Inject
    public void setAsyncService(InternalAsyncService asyncService) {
        this.asyncService = asyncService;
    }
}
