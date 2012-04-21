package net.contextfw.web.commons.async.internal.comet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.catalina.CometEvent;
import org.apache.catalina.CometProcessor;

import com.google.inject.Singleton;

@Singleton
public class TomcatAsyncServlet extends HttpServlet implements CometProcessor {

    private static final long serialVersionUID = 1L;

    public TomcatAsyncServlet() {
    }
    
    @Override
    public void event(CometEvent event) throws IOException, ServletException {
        TomcatAsyncService.getInstance().event(event);
    }
}
