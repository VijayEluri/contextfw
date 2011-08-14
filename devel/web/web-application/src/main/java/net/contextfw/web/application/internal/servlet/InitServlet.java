package net.contextfw.web.application.internal.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.internal.service.InitHandler;
import net.contextfw.web.application.lifecycle.RequestInvocation;
import net.contextfw.web.application.lifecycle.RequestInvocationFilter;
import net.contextfw.web.application.lifecycle.RequestInvocationFilter.Mode;

import com.google.inject.Inject;

public class InitServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    private final List<Class<? extends Component>> chain; // NOSONAR
    
    private final InitHandler handler;
    
    @Inject
    private final RequestInvocationFilter filter;
    
    private final RequestInvocation invocation = new RequestInvocation() {

        @Override
        public void invoke(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            handler.handleRequest(chain, InitServlet.this, request, response);
        }
    };
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        filter.filter(Mode.INIT, req, resp, invocation);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        filter.filter(Mode.INIT, req, resp, invocation);
    }

    public InitServlet(InitHandler handler,
                       List<Class<? extends Component>> chain,
                       RequestInvocationFilter filter) {
        this.handler = handler;
        this.chain = chain;
        this.filter = filter;
    }
}
