package net.contextfw.web.application.internal.service;

import java.io.PrintWriter;

public interface Responder {

    void setHeaders(String contentType);
    
    void sendError(int errorCode, String msg);
    
    void sendRedirect(String url);
    
    PrintWriter getWriter();
    
    void close();
}
