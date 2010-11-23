package net.contextfw.web.application.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.internal.service.WebApplicationContext;
import net.contextfw.web.application.internal.service.WebApplicationContextHandler;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public abstract class ContentServlet extends HttpServlet {

    @Inject
    private WebApplicationContextHandler handler;

    private static final long serialVersionUID = 1L;

    final protected <T> T doInWebApplicationScope(HttpServletRequest request, HttpServletResponse response,
                String handle, Executor<T> executor) {
        
        if (request == null) {
            throw new IllegalArgumentException("Request is required");
        } else if (response == null) {
            throw new IllegalArgumentException("Response is required");
        } else if (handle == null) {
            throw new IllegalArgumentException("Handle is required");
        } else if (executor == null) {
            throw new IllegalArgumentException("Executor is required");
        }
        
        WebApplicationContext app = handler.getContext(handle);

        if (app == null) {
            throw new WebApplicationException("Could not find web application");
        } else {
            synchronized (app) {
                app.getBeans().setAsCurrentInstance();
                app.getHttpContext().setServlet(this);
                app.getHttpContext().setRequest(request);
                app.getHttpContext().setResponse(response);
                T rv = null;
                try {
                    rv = executor.execute();
                } catch (Exception e) {
                    throw new WebApplicationException(e);
                } finally {
                    app.getHttpContext().setServlet(null);
                    app.getHttpContext().setRequest(null);
                    app.getHttpContext().setResponse(null);
                }
                return rv;
            }
        }
    }
}