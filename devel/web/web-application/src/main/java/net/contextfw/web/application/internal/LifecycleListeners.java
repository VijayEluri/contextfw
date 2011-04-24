package net.contextfw.web.application.internal;

import java.util.HashSet;
import java.util.Set;

import net.contextfw.web.application.LifecycleListener;
import net.contextfw.web.application.properties.Properties;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class LifecycleListeners implements LifecycleListener {

    private Set<LifecycleListener> listeners = new HashSet<LifecycleListener>();

    public void addListener(LifecycleListener listener) {
        listeners.add(listener);
    }
    
    @Inject
    public LifecycleListeners(Injector injector, Properties configuration) {
        if (configuration.get(Properties.LIFECYCLE_LISTENER) != null) {
            addListener(injector.getInstance(configuration.get(Properties.LIFECYCLE_LISTENER)));
        }
    }
    
    @Override
    public void beforeInitialize() {
        for (LifecycleListener listener : listeners) {
            listener.beforeInitialize();
        }
    }

    @Override
    public void afterInitialize() {
        for (LifecycleListener listener : listeners) {
            listener.afterInitialize();
        }
    }

    @Override
    public boolean beforeUpdate() {
        for (LifecycleListener listener : listeners) {
            if (!listener.beforeUpdate()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void afterUpdate() {
        for (LifecycleListener listener : listeners) {
            listener.afterUpdate();
        }        
    }

    @Override
    public void onException(Exception e) {
        for (LifecycleListener listener : listeners) {
            listener.onException(e);
        }
    }

    @Override
    public void beforeRender() {
        for (LifecycleListener listener : listeners) {
            listener.beforeRender();
        }
    }

    @Override
    public void afterRender() {
        for (LifecycleListener listener : listeners) {
            listener.afterRender();
        }
    }

    @Override
    public void onRefresh(String handle) {
        for (LifecycleListener listener : listeners) {
            listener.onRefresh(handle);
        }
    }

    @Override
    public void onRemove(String handle) {
        for (LifecycleListener listener : listeners) {
            listener.onRemove(handle);
        }        
    }
}
