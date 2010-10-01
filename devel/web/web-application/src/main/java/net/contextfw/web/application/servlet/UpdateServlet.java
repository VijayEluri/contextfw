package net.contextfw.web.application.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.internal.service.UpdateHandler;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class UpdateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handler.handleRequest(this, req, resp);
    }

    private static final long serialVersionUID = 1L;

    private final UpdateHandler handler;

    @Inject
    public UpdateServlet(UpdateHandler handler) {
        this.handler = handler;
    }
}
