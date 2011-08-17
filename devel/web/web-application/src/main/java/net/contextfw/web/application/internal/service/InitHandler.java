package net.contextfw.web.application.internal.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.HttpContext;
import net.contextfw.web.application.ResourceCleaner;
import net.contextfw.web.application.WebApplicationHandle;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.internal.component.MetaComponentException;
import net.contextfw.web.application.internal.page.PageScope;
import net.contextfw.web.application.internal.page.WebApplicationPage;
import net.contextfw.web.application.internal.servlet.UriMapping;
import net.contextfw.web.application.lifecycle.LifecycleListener;
import net.contextfw.web.application.lifecycle.PageFlowFilter;
import net.contextfw.web.application.remote.ErrorResolution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Provider;

public class InitHandler {

    private Logger logger = LoggerFactory.getLogger(InitHandler.class);
    
    private static final long HOUR = 1000*60*3600;

    // private final InitializerProvider initializers;
    @Inject
    private Provider<WebApplication> webApplicationProvider;
    @Inject
    private LifecycleListener listeners;
    @Inject
    private PageFlowFilter pageFlowFilter;
    @Inject
    private PageScope pageScope;

    private final long initialMaxInactivity;

    private DirectoryWatcher watcher;

    private ResourceCleaner cleaner;

    private final boolean developmentMode;

    public InitHandler(Configuration properties) {
        initialMaxInactivity = properties.get(Configuration.INITIAL_MAX_INACTIVITY);
        developmentMode = properties.get(Configuration.DEVELOPMENT_MODE);
    }

    public final void handleRequest(
            UriMapping mapping,
            List<Class<? extends Component>> chain,
            HttpServlet servlet,
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (watcher != null && watcher.hasChanged()) {
            logger.debug("Reloading resources");
            cleaner.clean();
        }

        if (!pageFlowFilter.beforePageCreate(
                pageScope.getPageCount(), request, response)) {
            return;
        }

        response.addHeader("Expires", "Sun, 19 Nov 1978 05:00:00 GMT");
        response.addHeader("Last-Modified", new Date().toString());
        response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.addHeader("Pragma", "no-cache");

        if (chain == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {

            WebApplicationPage page = createPage(
                    chain,
                    servlet,
                    request,
                    response);

            synchronized (page) {
                try {

                    pageFlowFilter.onPageCreate(
                                pageScope.getPageCount(),
                                pageFlowFilter.getRemoteAddr(request),
                                page.getHandle().getKey());

                    listeners.beforeInitialize();
                    page.getWebApplication().initState(mapping);
                    listeners.afterInitialize();
                    listeners.beforeRender();
                    boolean expired = page.getWebApplication().sendResponse();
                    listeners.afterRender();

                    // Setting expiration here so that long page processing is
                    // not
                    // penalizing client
                    if (expired) {
                        pageScope.refreshPage(page, 0);
                    } else {
                        pageScope.refreshPage(page, initialMaxInactivity);
                    }

                } catch (Exception e) {
                    // TODO Fix this construct with test
                    if (e instanceof MetaComponentException) {
                        ErrorResolution resolution =
                                ((MetaComponentException) e).getResolution();
                        if (resolution == ErrorResolution.SEND_NOT_FOUND_ERROR) {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND);
                        } else if (resolution == ErrorResolution.SEND_BAD_REQUEST_ERROR) {
                            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                        }
                    }
                    listeners.onException(e);
                } finally {
                    HttpContext context = page.getBean(Key.get(HttpContext.class));
                    context.setServlet(null);
                    context.setRequest(null);
                    context.setResponse(null);
                    pageScope.deactivateCurrentPage();
                }
            }
        }
    }

    private WebApplicationPage createPage(
            List<Class<? extends Component>> chain,
            HttpServlet servlet,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        WebApplicationPage page = pageScope.createPage(
                pageFlowFilter.getRemoteAddr(request),
                HOUR);
        
        page.setBean(Key.get(HttpContext.class), 
                new HttpContext(servlet, request, response));
        
        WebApplication app = webApplicationProvider.get();
        app.setInitializerChain(chain);
        page.setWebApplication(app);
        
        return page;
    }

    @Inject
    public void setWatcher(DirectoryWatcher watcher) {
        if (developmentMode) {
            this.watcher = watcher;
        }
    }

    @Inject
    public void setCleaner(ResourceCleaner cleaner) {
        if (developmentMode) {
            this.cleaner = cleaner;
        }
    }
}