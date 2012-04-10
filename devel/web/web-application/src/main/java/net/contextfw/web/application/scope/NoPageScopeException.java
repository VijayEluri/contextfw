package net.contextfw.web.application.scope;

import net.contextfw.web.application.PageHandle;
import net.contextfw.web.application.WebApplicationException;

public class NoPageScopeException extends WebApplicationException {

    private static final long serialVersionUID = 1L;

    public NoPageScopeException(PageHandle handle) {
        super("Page scope does not exist! handle = " + handle);
    }
    
}
