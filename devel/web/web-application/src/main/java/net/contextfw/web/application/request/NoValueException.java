package net.contextfw.web.application.request;

/**
 * <p>
 * This exception is thrown by the <code>RequestParameter</code> to indicate
 * that requested value cannot be found.
 * </p>
 * 
 * <p>
 * When request parameter value is accessed, it is not always certain that http
 * client has sent any values, and it must be indicated.
 * </p>
 * 
 * @see RequestParameter
 */
public class NoValueException extends Exception {

    private static final long serialVersionUID = 6369595507987932212L;

    /**
     * Sole constructor.
     */
    public NoValueException() {
    }
}