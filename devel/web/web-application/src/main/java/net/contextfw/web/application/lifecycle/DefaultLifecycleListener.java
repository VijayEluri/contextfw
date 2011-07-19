package net.contextfw.web.application.lifecycle;

import java.lang.reflect.Method;

import net.contextfw.web.application.component.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * The default implementation for LifecycleListener
 */
@Singleton
public class DefaultLifecycleListener implements LifecycleListener {
    
    private Logger logger = LoggerFactory.getLogger(DefaultLifecycleListener.class);

    @Override
    public void beforeInitialize() {
    }

    @Override
    public void afterInitialize() {
    }

    @Override
    public boolean beforeUpdate() {
        return true;
    }

    @Override
    public void afterUpdate() {
    }

    @Override
    public void onException(Exception e) {
        logger.error("Caught exception", e);
    }

    @Override
    public void beforeRender() {
    }

    @Override
    public void afterRender() {
    }

    @Override
    public boolean beforeRemotedMethod(Component component, Method method, Object[] args) {
        return true;
    }
}
