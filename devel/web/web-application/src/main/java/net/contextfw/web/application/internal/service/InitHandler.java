package net.contextfw.web.application.internal.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.contextfw.web.application.HttpContext;
import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.WebApplicationHandle;
import net.contextfw.web.application.WebApplicationServletModule;
import net.contextfw.web.application.elements.CElement;
import net.contextfw.web.application.internal.initializer.InitializerProvider;
import net.contextfw.web.application.internal.scope.WebApplicationScopedBeans;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class InitHandler {

    private Logger logger = LoggerFactory.getLogger(InitHandler.class);
    
    private final WebApplicationContextHandler handler;

    private final InitializerProvider initializers;

    private final Provider<WebApplication> webApplicationProvider;
    
    @Inject
    public InitHandler(WebApplicationContextHandler handler, 
            Provider<WebApplication> webApplicationProvider, 
            InitializerProvider initializers) {
        
        this.handler = handler;
        this.webApplicationProvider = webApplicationProvider;
        this.initializers = initializers;
    }

    public final void handleRequest(HttpServlet servlet,
            HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Class<? extends CElement>> chain = initializers.findChain(request.getRequestURI());
            
            if (chain == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
            
                WebApplicationContext context = prepareWebApplicationScope(servlet,
                        request, response);
                WebApplication app = webApplicationProvider.get();
                app.setInitializerChain(chain);
                context.setApplication(app);
                logger.debug("App created: {}", context.getHandle().getKey());
                handler.addContext(context);
    
                synchronized (context.getApplication()) {
                    app.initState();
                    app.sendResponse();
                    context.getHttpContext().setServlet(null);
                    context.getHttpContext().setRequest(null);
                    context.getHttpContext().setResponse(null);
                }
            }
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }

        response.getWriter().close();
    }

    private WebApplicationContext prepareWebApplicationScope(HttpServlet servlet, HttpServletRequest request,
            HttpServletResponse response) {
        WebApplicationScopedBeans beans = WebApplicationScopedBeans
                .createNewInstance();
        HttpContext httpContext = new HttpContext(servlet, request, response);
        WebApplicationContext context = new WebApplicationContext(httpContext,
                handler.createNewHandle(), beans);
        beans.seed(HttpContext.class, httpContext);
        beans.seed(WebApplicationHandle.class, context.getHandle());
        return context;
    }
}