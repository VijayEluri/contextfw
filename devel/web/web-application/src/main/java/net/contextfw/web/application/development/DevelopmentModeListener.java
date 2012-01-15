package net.contextfw.web.application.development;

/**
 * <p>Informs when classes or resources have been reloaded</p>
 */
public interface DevelopmentModeListener {
    
    /**
     * <p>Informs when classes have been reloaded</p>
     * 
     * @param classLoader
     *   The new class loader that is used to load classes
     */
    void classesReloaded(ClassLoader classLoader);
    
    /**
     * <p>Informs when resources have been reloaded</p>
     */
    void resourcesReloaded();
}
