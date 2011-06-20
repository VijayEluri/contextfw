package net.contextfw.web.application.internal;

import java.util.HashSet;
import java.util.Set;

import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.lifecycle.LifecycleListener;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class LifecycleListeners implements LifecycleListener {

    private Set<LifecycleListener> listeners = new HashSet<LifecycleListener>();

    public void addListener(LifecycleListener listener) {
        listeners.add(listener);
    }
    
    @SuppressWarnings("unchecked")
    @Inject
    public LifecycleListeners(Injector injector, Configuration configuration) {
        if (configuration.get(Configuration.LIFECYCLE_LISTENER) != null) {
            LifecycleListener listener = null;
            Object obj = configuration.get(Configuration.LIFECYCLE_LISTENER);
            if (obj instanceof LifecycleListener) {
                listener = (LifecycleListener) obj;
            } else {
                listener = injector.getInstance((Class<LifecycleListener>) obj);
            }
            addListener(listener);
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
}
