package net.contextfw.web.application.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.internal.service.InitHandler;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * - get/post
 * - create new web application
 * - let web application handle everything
 *    - create root element; (WebApplicationElement)
 *    - find initializer chain for url
 *    - Start initializing
 *    - Loop through initializer chain:
 *       - Create InitializerContextImpl, initialize
 *       - Initialize first element in the chain. If element contains method initialize(Context..) call it
 *          - if context.initializeChild is called initialize it. do recursively.
 *    - After initializing, check if redirect or error response is invoked
 *      - if redirect, do rediect
 *      - if error, do error
 *      - else, register root as child to web applicationelement,
 *      call build();
 * @author marko
 *
 */

@Singleton
public class InitServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handler.handleRequest(this, req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handler.handleRequest(this, req, resp);
    }

    private static final long serialVersionUID = 1L;

    private final InitHandler handler;

    @Inject
    public InitServlet(InitHandler handler) {
        this.handler = handler;
    }
}
