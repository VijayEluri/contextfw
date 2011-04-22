package net.contextfw.web.application.internal.service;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.PageFlowFilter;
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

    private final PageFlowFilter pageFlowFilter;

    @Inject
    public UpdateHandler(
            WebApplicationContextHandler handler,
            WebConfiguration configuration,
            LifecycleListeners listeners,
            PageFlowFilter pageFlowFilter) {
        this.handler = handler;
        this.configuration = configuration;
        this.listeners = listeners;
        this.pageFlowFilter = pageFlowFilter;
    }

    public final void handleRequest(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String[] uriSplits = request.getRequestURI().split("/");

        if (uriSplits.length > 2) {

            String command = uriSplits[uriSplits.length - 2];
            String handlerStr = uriSplits[uriSplits.length - 1];

            if (!"contextfw-remove".equals(command)) {
                if (!pageFlowFilter.beforePageUpdate(handler.getContextCount(),
                        request)) {
                    return;
                }
            }
            
            String remoteAddr = pageFlowFilter.getRemoteAddr(request);

            WebApplicationContext app = handler.getContext(handlerStr);

            if (app == null || app.getExpires() < System.currentTimeMillis()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else if (!app.getRemoteAddr().equals(remoteAddr)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                logger.info("Trying to refresh page scope from different ip:" +
                            " original = {}, current = {}",
                            app.getRemoteAddr(), remoteAddr);
            } else {
                synchronized (app) {

                    if ("contextfw-remove".equals(command)) {
                        listeners.onRemove(handlerStr);
                        handler.removeApplication(app.getHandle());

                        pageFlowFilter.pageRemoved(
                                    handler.getContextCount(),
                                    remoteAddr,
                                    handlerStr);

                        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    } else {
                        int updateCount = handler.refreshApplication(app.getHandle());
                        app.getBeans().setAsCurrentInstance();
                        app.getHttpContext().setServlet(servlet);
                        app.getHttpContext().setRequest(request);
                        app.getHttpContext().setResponse(response);
                        try {
                            pageFlowFilter.onPageUpdate(
                                        handler.getContextCount(),
                                        remoteAddr,
                                        handlerStr,
                                        updateCount);
                            if ("contextfw-update".equals(command)) {
                                boolean cont = app.getApplication().updateState(listeners.beforeUpdate());
                                if (!cont) {
                                    app.getHttpContext().setServlet(null);
                                    app.getHttpContext().setRequest(null);
                                    app.getHttpContext().setResponse(null);
                                    return;
                                }
                                listeners.afterUpdate();
                                listeners.beforeRender();
                                setHeaders(response);
                                response.setContentType("text/xml; charset=UTF-8");
                                app.getApplication().sendResponse();
                                listeners.afterRender();
                            } else if ("contextfw-refresh".equals(command)) {
                                listeners.onRefresh(handlerStr);
                                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                            }
                        } catch (Exception e) {
                            listeners.onException(e);
                        } finally {
                            app.getHttpContext().setServlet(null);
                            app.getHttpContext().setRequest(null);
                            app.getHttpContext().setResponse(null);
                        }
                    }
                }
                response.getWriter().close();
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    public void setHeaders(HttpServletResponse response) {
        response.addHeader("Expires", "Sun, 19 Nov 1978 05:00:00 GMT");
        response.addHeader("Last-Modified", new Date().toString());
        response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // response.addHeader("Cache-Control","post-check=0, pre-check=0");
        response.addHeader("Pragma", "no-cache");
        response.setHeader("Connection", "Keep-Alive");
    }
}