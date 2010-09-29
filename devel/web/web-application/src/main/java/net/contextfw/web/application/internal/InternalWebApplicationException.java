package net.contextfw.web.application.internal;

public class InternalWebApplicationException extends RuntimeException {

    private final static String PREFIX = "** THIS IS AN INTERNAL EXCEPTION. PLEASE REPORT IT AS A BUG **\n";
    
    private static final long serialVersionUID = 1L;

    public InternalWebApplicationException() {
        super(PREFIX);
    }

    public InternalWebApplicationException(String msg, Throwable throwable) {
        super(PREFIX + msg, throwable);
    }

    public InternalWebApplicationException(String msg) {
        super(PREFIX + msg);
    }

    public InternalWebApplicationException(Throwable throwable) {
        super(PREFIX, throwable);
    }
}
