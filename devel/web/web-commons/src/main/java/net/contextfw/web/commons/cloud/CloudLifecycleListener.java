package net.contextfw.web.commons.cloud;

import java.lang.reflect.Method;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.lifecycle.DefaultLifecycleListener;
import net.contextfw.web.commons.cloud.session.CloudSession;
import net.contextfw.web.commons.cloud.session.OpenMode;

import com.google.inject.Inject;

public class CloudLifecycleListener extends DefaultLifecycleListener {

    @Inject
    private CloudSession session;
    
    @Override
    public void beforeInitialize() {
        session.openSession(OpenMode.LAZY);
        super.beforeInitialize();
    }

    @Override
    public void afterInitialize() {
        session.closeSession();
        super.afterInitialize();
    }

    @Override
    public boolean beforeUpdate(Component component, Method method, Object[] args) {
        session.openSession(OpenMode.LAZY);
        return super.beforeUpdate(component, method, args);
    }

    @Override
    public void afterUpdate(Component component, Method method, RuntimeException thrown) {
        session.closeSession();
        super.afterUpdate(component, method, thrown);
    }
}
