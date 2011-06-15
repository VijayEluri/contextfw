package net.contextfw.web.application.internal.service;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.ResourceCleaner;
import net.contextfw.web.application.internal.LifecycleListeners;
import net.contextfw.web.application.lifecycle.PageFlowFilter;
import net.contextfw.web.application.properties.Properties;
import net.contextfw.web.application.remote.ResourceResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class UpdateHandler {

    private static final String CONTEXTFW_REFRESH = "contextfw-refresh";

    private static final String CONTEXTFW_UPDATE = "contextfw-update";

    private static final String CONTEXTFW_REMOVE = "contextfw-remove";

    private Logger logger = LoggerFactory.getLogger(UpdateHandler.class);

    private final WebApplicationContextHandler handler;

    private final LifecycleListeners listeners;

    private final PageFlowFilter pageFlowFilter;

    @Inject
    private Gson gson;
    
    private final DirectoryWatcher watcher;
    
    private final ResourceCleaner cleaner;

    
    @Inject
    public UpdateHandler(
            WebApplicationContextHandler handler,
            LifecycleListeners listeners,
            PageFlowFilter pageFlowFilter,
            DirectoryWatcher watcher,
            ResourceCleaner cleaner,
            Properties configuration) {
    	
        this.handler = handler;
        this.listeners = listeners;
        this.pageFlowFilter = pageFlowFilter;
        
        if (configuration.get(Properties.DEVELOPMENT_MODE)) {
        	this.cleaner = cleaner;
        	this.watcher = watcher;
        } else {
        	this.cleaner = null;
        	this.watcher = null;
        }
    }

    private int getCommandStart(String[] splits) {
        if (splits.length > 2) {
            String command = splits[splits.length - 2];
            if  (CONTEXTFW_REMOVE.equals(command) || 
                 CONTEXTFW_REFRESH.equals(command)) {
                return splits.length - 2;
            }
        }
        if (splits.length > 4) {
            String command = splits[splits.length - 4];
            if  (CONTEXTFW_UPDATE.equals(command)) {
                return splits.length -4;
            }
        }
        if (splits.length > 5) {
            String command = splits[splits.length - 5];
            if (CONTEXTFW_UPDATE.equals(command)) {
                return splits.length -5;
            }
        }
        return -1;
    }
    
    public final void handleRequest(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	if (watcher != null && watcher.hasChanged()) {
    		logger.info("Reloading resources");
    		cleaner.clean();
    	}
    	
        String[] uriSplits = request.getRequestURI().split("/");
        int commandStart = getCommandStart(uriSplits); 
        if (commandStart != -1) {
            
            String command = uriSplits[commandStart];
            String handlerStr = uriSplits[commandStart + 1];

            if (!CONTEXTFW_REMOVE.equals(command)) {
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
                UpdateInvocation invocation = UpdateInvocation.NOT_DELAYED;
                synchronized (app) {

                    if (CONTEXTFW_REMOVE.equals(command)) {
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
                            if (CONTEXTFW_UPDATE.equals(command)) {
                                invocation = 
                                    app.getApplication().updateState(
                                            listeners.beforeUpdate(), 
                                            uriSplits[commandStart+2],
                                            uriSplits[commandStart+3]);
                                if (invocation.isDelayed()) {
                                    app.getHttpContext().setServlet(null);
                                    app.getHttpContext().setRequest(null);
                                    app.getHttpContext().setResponse(null);
                                    return;
                                }
                                listeners.afterUpdate();
                                
                                if (!invocation.isResource()) {
                                    listeners.beforeRender();
                                    setHeaders(response);
                                    response.setContentType("text/xml; charset=UTF-8");
                                    app.getApplication().sendResponse();
                                    listeners.afterRender();
                                }
                            } else if (CONTEXTFW_REFRESH.equals(command)) {
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
                if (invocation.isResource()) {
                    handleResource(request, response, invocation);
                } else {
                    response.getWriter().close();
                }
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void handleResource(HttpServletRequest request, HttpServletResponse response,
            UpdateInvocation invocation) throws IOException {
        if (invocation.getRetVal() == null) {
            response.getWriter().close();
            return;
        }
        if (invocation.getRetVal() instanceof ResourceResponse) {
            ((ResourceResponse) invocation.getRetVal()).serve(request, response);
        } else {
            setHeaders(response);
            response.setContentType("application/json; charset=UTF-8");
            gson.toJson(invocation.getRetVal(), response.getWriter());
            response.getWriter().close();
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