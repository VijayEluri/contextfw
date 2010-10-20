package net.contextfw.web.application;

/**
 * A general exception thrown by application
 */ 
public class WebApplicationException extends RuntimeException {
    private static final long serialVersionUID = -3864752109086700032L;

    public WebApplicationException() {
        super();
    }

    public WebApplicationException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public WebApplicationException(String arg0) {
        super(arg0);
    }

    public WebApplicationException(Throwable arg0) {
        super(arg0);
    }

}
