package net.contextfw.web.commons.async;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import net.contextfw.web.commons.async.internal.comet.TomcatAsyncService;

import org.apache.catalina.CometEvent;
import org.apache.catalina.CometProcessor;

public class TomcatAsyncServlet extends HttpServlet implements CometProcessor {

    private static final long serialVersionUID = 1L;

    public TomcatAsyncServlet() {
    }
    
    @Override
    public void event(CometEvent event) throws IOException, ServletException {
        TomcatAsyncService.getInstance().event(event);
    }
}
