package net.contextfw.web.commons.cloud.session;

import java.lang.reflect.Method;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.lifecycle.DefaultLifecycleListener;

import com.google.inject.Inject;

public class CloudSessionLifecycleListener extends DefaultLifecycleListener {

    private final CloudSession session;
    
    @Inject
    public CloudSessionLifecycleListener(CloudSession session) {
        this.session = session;
    }
    
    @Override
    public void beforeInitialize() {
        session.openSession(OpenMode.LAZY);
        super.beforeInitialize();
    }

    @Override
    public boolean beforeUpdate(Component component, Method method, Object[] args) {
        CloudSessionOpenMode annotation = method.getAnnotation(CloudSessionOpenMode.class);
        session.openSession(annotation == null ? OpenMode.LAZY : annotation.value());
        return super.beforeUpdate(component, method, args);
    }

    @Override
    public void onException(Exception e) {
        super.onException(e);
    }
    
    @Override
    public void beforePageScopeDeactivation() {
        session.closeSession();
    }
}
