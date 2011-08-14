package net.contextfw.web.application.remote;

/**
 * Denotes the resolutions what should be done on exceptional case.
 */
public enum ErrorResolution {
    /**
     * Sets the variable to null.
     * 
     * <p>
     *  Note, if underlaying parameter is primitive, this may fail and throw another
     *  exception. That exception is not caught.
     * </p>
     */
    SET_TO_NULL,
    /**
     * Rethrows the exception causing the failure forward.
     */
    RETHROW_CAUSE,
    /**
     * Stop the initialization from being executed and send Not Found (404) error
     * to client.
     */
    SEND_NOT_FOUND_ERROR,
    /**
     * Stop the initialization from being executed and send Bad Request (400) error
     * to client.
     */
    SEND_BAD_REQUEST_ERROR
}
