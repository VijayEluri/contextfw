package net.contextfw.web.application.internal.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.HttpContext;
import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.WebApplicationHandle;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.internal.LifecycleListeners;
import net.contextfw.web.application.internal.initializer.InitializerProvider;
import net.contextfw.web.application.internal.scope.WebApplicationScopedBeans;
import net.contextfw.web.application.lifecycle.PageFlowFilter;
import net.contextfw.web.application.properties.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class InitHandler {

    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger(InitHandler.class);

    private final WebApplicationContextHandler handler;

    private final InitializerProvider initializers;

    private final Provider<WebApplication> webApplicationProvider;

    private final LifecycleListeners listeners;

    private final PageFlowFilter pageFlowFilter;
    
    private final long initialMaxInactivity;

    @Inject
    public InitHandler(WebApplicationContextHandler handler,
                       Provider<WebApplication> webApplicationProvider,
                       InitializerProvider initializers,
                       LifecycleListeners listeners,
                       Properties configuration,
                       PageFlowFilter pageFlowFilter) {

        this.handler = handler;
        this.webApplicationProvider = webApplicationProvider;
        this.initializers = initializers;
        this.listeners = listeners;
        this.pageFlowFilter = pageFlowFilter;
        initialMaxInactivity = configuration.get(Properties.INITIAL_MAX_INACTIVITY);
    }

    public final void handleRequest(HttpServlet servlet,
            HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        if(!pageFlowFilter.beforePageCreate(
                handler.getContextCount(),
                    request)) {
            return;
        }

        response.addHeader("Expires", "Sun, 19 Nov 1978 05:00:00 GMT");
        response.addHeader("Last-Modified", new Date().toString());
        response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.addHeader("Pragma", "no-cache");

        List<Class<? extends Component>> chain = initializers.findChain(request.getRequestURI());

        if (chain == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {

            WebApplicationContext context = prepareWebApplicationScope(servlet,
                        request, response);
            WebApplication app = webApplicationProvider.get();
            app.setInitializerChain(chain);
            context.setApplication(app);
            handler.addContext(context);

            synchronized (context.getApplication()) {
                try {

                    pageFlowFilter.onPageCreate(
                                handler.getContextCount(),
                                pageFlowFilter.getRemoteAddr(request),
                                context.getHandle().getKey());

                    listeners.beforeInitialize();
                    app.initState();
                    listeners.afterInitialize();
                    listeners.beforeRender();
                    app.sendResponse();
                    listeners.afterRender();

                    // Setting expiration here so that long page processing is
                    // not
                    // penalizing client

                    context.setExpires(System.currentTimeMillis() + initialMaxInactivity);

                } catch (Exception e) {
                    listeners.onException(e);
                    throw new WebApplicationException(e);
                } finally {
                    context.getHttpContext().setServlet(null);
                    context.getHttpContext().setRequest(null);
                    context.getHttpContext().setResponse(null);
                }

            }
        }

        response.getWriter().close();
    }

    private WebApplicationContext prepareWebApplicationScope(HttpServlet servlet, HttpServletRequest request,
            HttpServletResponse response) {
        WebApplicationScopedBeans beans = WebApplicationScopedBeans
                .createNewInstance();
        HttpContext httpContext = new HttpContext(servlet, request, response);
        WebApplicationContext context = new WebApplicationContext(httpContext,
                pageFlowFilter.getRemoteAddr(request),
                System.currentTimeMillis() + initialMaxInactivity,
                handler.createNewHandle(), beans);
        beans.seed(HttpContext.class, httpContext);
        beans.seed(WebApplicationHandle.class, context.getHandle());
        return context;
    }
}