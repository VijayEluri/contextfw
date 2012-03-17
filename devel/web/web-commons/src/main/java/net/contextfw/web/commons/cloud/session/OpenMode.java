package net.contextfw.web.commons.cloud.session;

/**
 * Indicates the mode where session should be opened
 */
public enum OpenMode {
    
    /**
     * Opens session eagerly.
     * <p>
     * When session is opened in eager mode, it is created immediately, even if session is never
     * used.  
     * </p>
     */
    EAGER, 
    
    /**
     * Opens session lazily when needed.
     * 
     * <p>
     * If session is opened in lazy-mode, it is opened only after it is really needed. This option
     * is probably the best approach since, it does not create unnecessary sessions.
     */
    LAZY,
    
    /**
     * Opens an existing session.
     * 
     * <p>
     * In this mode, it is required that session already exists. Opening non-existing session in 
     * this mode, will throw an exception.
     * </p>
     * 
     * <p>
     * This mode has one major advantage. When session is opened in existing-mode, the valid through
     * time of the session is not extended. It creates a possibility to create polling remote
     * methods that does not force the session to be open forever.
     * </p>
     */
    EXISTING
}
