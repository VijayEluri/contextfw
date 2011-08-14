package net.contextfw.web.application.internal.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.internal.service.UpdateHandler;
import net.contextfw.web.application.lifecycle.RequestInvocation;
import net.contextfw.web.application.lifecycle.RequestInvocationFilter;
import net.contextfw.web.application.lifecycle.RequestInvocationFilter.Mode;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class UpdateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final transient UpdateHandler handler;
    
    private final RequestInvocationFilter filter;
    
    private final RequestInvocation invocation = new RequestInvocation() {
        @Override
        public void invoke(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            handler.handleRequest(UpdateServlet.this, request, response);
        }
    };
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        filter.filter(Mode.UPDATE, req, resp, invocation);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        filter.filter(Mode.UPDATE, req, resp, invocation);
    }

    @Inject
    public UpdateServlet(UpdateHandler handler, 
                         RequestInvocationFilter filter) {
        this.handler = handler;
        this.filter = filter;
    }
}
