package net.contextfw.web.commons.cloud.session;

import java.lang.reflect.Method;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.lifecycle.DefaultLifecycleListener;

import com.google.inject.Inject;

/**
 * Default implementation for lifecycle listener for cloud sessions.
 * 
 * <p>
 *  It is recommended to use this class as base class, or just create own using this 
 *  the source of this class as a template.
 * </p>
 * 
 * @author marko.lavikainen@netkoti.fi
 *
 */
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
    public void beforePageScopeDeactivation() {
        session.closeSession();
    }
}