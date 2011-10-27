package net.contextfw.web.application.lifecycle;

import java.io.IOException;

import net.contextfw.web.application.WebApplication;
import net.contextfw.web.application.WebApplicationHandle;

public interface WebApplicationStorage {

    boolean remove(WebApplicationHandle handle);
    
    Integer refresh(WebApplicationHandle handle, 
                    String remoteAddr,
                    long maxInactivity);
    
    WebApplicationHandle createHandle();
    
    void execute(WebApplicationHandle handle,
                 String remoteAddr,
                 ScopedExecution execution) throws IOException;
    
    void execute(WebApplicationHandle handle, 
                 WebApplication application,
                 String remoteAddr,
                 ScopedExecution execution) throws IOException;
    
    int getPageCount();
}
