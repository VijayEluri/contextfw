package net.contextfw.web.application.scope;

import net.contextfw.web.application.WebApplication;

public interface ScopedWebApplicationExecution {
    
    void execute(WebApplication application);
}
