package net.contextfw.web.application.internal.service;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.conf.WebConfiguration;
import net.contextfw.web.application.internal.LifecycleListeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class UpdateHandler {

    private Logger logger = LoggerFactory.getLogger(UpdateHandler.class);
    
    private final WebApplicationContextHandler handler;

    private final WebConfiguration configuration;

    private final LifecycleListeners listeners;
    
    @Inject
    public UpdateHandler(
            WebApplicationContextHandler handler, 
            WebConfiguration configuration, 
            LifecycleListeners listeners) {
        this.handler = handler;
        this.configuration = configuration;
        this.listeners = listeners;
    }

    public final void handleRequest(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            
            response.addHeader("Expires", "Sun, 19 Nov 1978 05:00:00 GMT");
            response.addHeader("Last-Modified", new Date().toString());
            response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            //response.addHeader("Cache-Control","post-check=0, pre-check=0");
            response.addHeader("Pragma", "no-cache");
            response.setHeader("Connection", "Keep-Alive");
            
            String[] uriSplits = request.getRequestURI().split("/");

            if (uriSplits.length > 2) {

                String command = uriSplits[uriSplits.length - 2];
                String handlerStr = uriSplits[uriSplits.length - 1];

                WebApplicationContext app = handler.getContext(handlerStr);

                if (app == null) {
                    Thread.sleep(configuration.getErrorTime());
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    synchronized (app) {

                        if ("contextfw-remove".equals(command)) {
                            handler.removeApplication(app.getHandle());
                            response.setStatus(HttpServletResponse.SC_OK);
                        } else {

                            handler.refreshApplication(app.getHandle());

                            if ("contextfw-update".equals(command)) {

                                response.setContentType("text/xml; charset=UTF-8");
                                app.getBeans().setAsCurrentInstance();
                                app.getHttpContext().setServlet(servlet);
                                app.getHttpContext().setRequest(request);
                                app.getHttpContext().setResponse(response);
                                try {
                                    listeners.beforeUpdate();
                                    app.getApplication().updateState();
                                    listeners.afterUpdate();
                                    listeners.beforeRender();
                                    app.getApplication().sendResponse();
                                    listeners.afterRender();
                                } catch (Exception e) {
                                    listeners.onException(e);
                                } finally {
                                    app.getHttpContext().setServlet(null);
                                    app.getHttpContext().setRequest(null);
                                    app.getHttpContext().setResponse(null);
                                }
                            } else if ("contextfw-refresh".equals(command)) {
                                response.setStatus(HttpServletResponse.SC_OK);
                            }
                        }
                    }
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception npe) {
            logger.error("Exception", npe);
        }
        response.getWriter().close();
    }
}