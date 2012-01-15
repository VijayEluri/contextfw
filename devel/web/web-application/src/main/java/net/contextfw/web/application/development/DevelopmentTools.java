package net.contextfw.web.application.development;

/**
 * <p>Provides information about system behavior during development time</p> 
 * 
 * <p>This interface must be injected by Guice.</p>
 */
public interface DevelopmentTools {

    /**
     * Adds a new listener to the tools
     */
    void addListener(DevelopmentModeListener listener);
    
    boolean isDevelopmentMode();
}
