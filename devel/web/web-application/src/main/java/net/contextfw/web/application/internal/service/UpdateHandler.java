package net.contextfw.web.application.internal.service;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.ResourceCleaner;
import net.contextfw.web.application.WebApplicationHandle;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.internal.page.PageScope;
import net.contextfw.web.application.internal.page.WebApplicationPage;
import net.contextfw.web.application.lifecycle.LifecycleListener;
import net.contextfw.web.application.lifecycle.PageFlowFilter;
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

    private final LifecycleListener listeners;

    private final PageFlowFilter pageFlowFilter;

    @Inject
    private Gson gson;
    
    private final DirectoryWatcher watcher;
    
    private final ResourceCleaner cleaner;

    private final PageScope pageScope;
    
    private final long maxInactivity;
    
    @Inject
    public UpdateHandler(
            PageScope pageScope,
            LifecycleListener listeners,
            PageFlowFilter pageFlowFilter,
            DirectoryWatcher watcher,
            ResourceCleaner cleaner,
            Configuration configuration) {
    	
        this.pageScope = pageScope;
        this.listeners = listeners;
        this.pageFlowFilter = pageFlowFilter;
        this.maxInactivity = configuration.get(Configuration.MAX_INACTIVITY);
        
        if (configuration.get(Configuration.DEVELOPMENT_MODE)) {
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
    
    public final void handleRequest(HttpServlet servlet, 
                                    HttpServletRequest request, 
                                    HttpServletResponse response)
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
                if (!pageFlowFilter.beforePageUpdate(
                        pageScope.getPageCount(),
                        request, 
                        response)) {
                    return;
                }
            }
            
            String remoteAddr = pageFlowFilter.getRemoteAddr(request);

            WebApplicationPage page = pageScope.findPage(
                    new WebApplicationHandle(handlerStr),                  
                    remoteAddr);

            if (page == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                UpdateInvocation invocation = UpdateInvocation.NOT_DELAYED;
                synchronized (page) {
                    
                    pageScope.activatePage(page, servlet, request, response);

                    if (CONTEXTFW_REMOVE.equals(command)) {
                        pageScope.removePage(page.getHandle());

                        pageFlowFilter.pageRemoved(
                                    pageScope.getPageCount(),
                                    remoteAddr,
                                    handlerStr);

                        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    } else {
                        int updateCount = pageScope.refreshPage(page, maxInactivity);
                        try {
                            pageFlowFilter.onPageUpdate(
                                        pageScope.getPageCount(),
                                        remoteAddr,
                                        handlerStr,
                                        updateCount);
                            if (CONTEXTFW_UPDATE.equals(command)) {
                                invocation = 
                                    page.getWebApplication().updateState(
                                            listeners.beforeUpdate(), 
                                            uriSplits[commandStart+2],
                                            uriSplits[commandStart+3]);
                                if (invocation.isDelayed()) {
                                    pageScope.deactivateCurrentPage();
                                    return;
                                }
                                listeners.afterUpdate();
                                
                                if (!invocation.isResource()) {
                                    listeners.beforeRender();
                                    setHeaders(response);
                                    response.setContentType("text/xml; charset=UTF-8");
                                    page.getWebApplication().sendResponse();
                                    listeners.afterRender();
                                }
                            } else if (CONTEXTFW_REFRESH.equals(command)) {
                                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                            }
                        } catch (Exception e) {
                            listeners.onException(e);
                        } finally {
                            pageScope.deactivateCurrentPage();
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