package net.contextfw.web.application.internal.service;

import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleResponder implements Responder {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleResponder.class);
    
    private final PrintWriter out;
    
    public SimpleResponder(PrintWriter out) {
        this.out = out;
    }
    
    @Override
    public void setHeaders(String contentType) {
    }

    @Override
    public void sendError(int errorCode, String msg) {
    }

    @Override
    public void sendRedirect(String url) {
    }

    @Override
    public PrintWriter getWriter() {
        return out;
    }

    @Override
    public void close() {
        try {
            out.close();
        } catch (Exception e) {
            LOG.debug("Exception whil closing", e);
        }
    }

}
