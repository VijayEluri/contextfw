package net.contextfw.web.application.internal.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.WebApplicationException;

public class ServletResponder implements Responder {

    private final HttpServletResponse response;
    
    public ServletResponder(HttpServletResponse response) {
        this.response = response;
    }
    
    @Override
    public void setHeaders(String contentType) {
        response.addHeader("Expires", "Sun, 19 Nov 1978 05:00:00 GMT");
        response.addHeader("Last-Modified", new Date().toString());
        response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // response.addHeader("Cache-Control","post-check=0, pre-check=0");
        response.addHeader("Pragma", "no-cache");
        response.setHeader("Connection", "Keep-Alive");
        response.setHeader("X-Powered-By", "www.contextfw.net");
        response.setContentType(contentType);        
    }

    @Override
    public PrintWriter getWriter() {
        try {
            return response.getWriter();
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
    }

    @Override
    public void close() {
        try {
            response.getWriter().close();
        } catch (Exception e) {
            // Ignored
        }
    }

    @Override
    public void sendError(int errorCode, String msg) {
        try {
            response.sendError(errorCode, msg);
        } catch (IOException e) {
            throw new WebApplicationException(e);
        } 
    }

    @Override
    public void sendRedirect(String url) {
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }        
    }
}
