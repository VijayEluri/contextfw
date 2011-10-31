package net.contextfw.web.application.internal.development;

import java.util.HashSet;
import java.util.Set;

import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.development.DevelopmentModeListener;
import net.contextfw.web.application.development.DevelopmentTools;

public class DevelopmentToolsImpl implements DevelopmentTools, InternalDevelopmentTools {

    private final Set<DevelopmentModeListener> listeners = 
            new HashSet<DevelopmentModeListener>();

    private final ClassLoaderProvider classLoaderProvider;
    
    private final boolean developmentMode;
    
    public DevelopmentToolsImpl(Configuration configuration) {
        classLoaderProvider = new ClassLoaderProvider(
                new ReloadingClassLoaderConf(configuration));
        
        developmentMode = configuration.get(Configuration.DEVELOPMENT_MODE);
    }
    
    @Override
    public void addListener(DevelopmentModeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    @Override
    public boolean isDevelopmentMode() {
        return developmentMode;
    }
    
    @Override
    public ClassLoader reloadClasses() {
        ClassLoader classLoader = classLoaderProvider.reload();
        for (DevelopmentModeListener listener : listeners) {
            listener.classesReloaded(classLoader);
        }
        return classLoader;
    }
    
    @Override
    public void reloadResources() {
        for (DevelopmentModeListener listener : listeners) {
            listener.resourcesReloaded();
        }
    }
}
