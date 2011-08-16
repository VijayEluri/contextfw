package net.contextfw.web.application.lifecycle;

import static org.junit.Assert.assertTrue;
import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.lifecycle.DefaultLifecycleListener;
import net.contextfw.web.application.lifecycle.LifecycleListener;

import org.junit.Test;

public class LifecycleListenerTest {

    private LifecycleListener listener = new DefaultLifecycleListener();
    
    @Test
    public void Basic_Test() {
        assertTrue(listener.beforeUpdate());
        assertTrue(listener.beforeRemotedMethod(null, null, null));
        // Tests that with null exception no exception is thrown
        listener.afterRemoteMethod(null, null, null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void After_Remote_Method_Trows_Exception() {
        listener.afterRemoteMethod(null, null, new IllegalArgumentException());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void On_Exception_Throws_Runtime_Exception() {
        listener.onException(new IllegalArgumentException());
    }
    
    @Test(expected=WebApplicationException.class)
    public void On_Exception_Throws_Web_Application_Exception() {
        listener.onException(new NoSuchMethodException());
    }
    
}
