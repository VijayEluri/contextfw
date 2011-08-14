package net.contextfw.web.application.lifecycle;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RequestInvocationFilter {

    public enum Mode {
        INIT, UPDATE
    }
    
    public void filter(Mode mode, HttpServletRequest request, 
                       HttpServletResponse response, 
                       RequestInvocation invocation) throws ServletException, IOException;
}
