package net.contextfw.web.application.lifecycle;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultRequestInvocationFilter implements RequestInvocationFilter {

    @Override
    public void filter(Mode mode, HttpServletRequest request, 
                       HttpServletResponse response,
                       RequestInvocation invocation) throws ServletException, IOException {
        
        invocation.invoke(request, response);
    }

}
