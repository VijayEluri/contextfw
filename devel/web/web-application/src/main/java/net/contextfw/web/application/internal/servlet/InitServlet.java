package net.contextfw.web.application.internal.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.internal.service.InitHandler;

import com.google.inject.Inject;

public class InitServlet extends HttpServlet {

    private final List<Class<? extends Component>> chain;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handler.handleRequest(chain, this, req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handler.handleRequest(chain, this, req, resp);
    }

    private static final long serialVersionUID = 1L;

    private final InitHandler handler;

    @Inject
    public InitServlet(InitHandler handler, List<Class<? extends Component>> chain) {
        this.handler = handler;
        this.chain = chain;
    }
}
