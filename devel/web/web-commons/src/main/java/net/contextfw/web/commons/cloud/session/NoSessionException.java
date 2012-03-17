package net.contextfw.web.commons.cloud.session;

import net.contextfw.web.application.WebApplicationException;

/**
 * Thrown if operation requires an existing session .
 */
public class NoSessionException extends WebApplicationException {

    private static final long serialVersionUID = 1L;
    
    public NoSessionException() {
        super();
    }

    public NoSessionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public NoSessionException(String msg) {
        super(msg);
    }

    public NoSessionException(Throwable cause) {
        super(cause);
    }
}
