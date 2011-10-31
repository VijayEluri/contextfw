package net.contextfw.web.application.scope;

import javax.servlet.http.HttpServletRequest;

import net.contextfw.web.application.WebApplication;
import net.contextfw.web.application.WebApplicationHandle;

public interface WebApplicationStorage {
    
    void initialize(WebApplication application,
                    HttpServletRequest request,
                    long validThrough,
                    ScopedWebApplicationExecution execution);

    void update(WebApplicationHandle handle,
                HttpServletRequest request,
                long validThrough,
                ScopedWebApplicationExecution execution);
    
    void execute(WebApplicationHandle handle,
                 ScopedWebApplicationExecution execution);

    void refresh(WebApplicationHandle handle, 
             HttpServletRequest request,
             long validThrough);

    void remove(WebApplicationHandle handle,
            HttpServletRequest request);
}
