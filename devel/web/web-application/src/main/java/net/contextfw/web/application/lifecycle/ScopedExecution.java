package net.contextfw.web.application.lifecycle;

import java.io.IOException;

import net.contextfw.web.application.WebApplication;

public interface ScopedExecution {
    
    void execute(WebApplication application) throws IOException;
}
