package net.contextfw.web.commons.cloud.session;

import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import net.contextfw.web.commons.AbstractTest;

import org.junit.Test;

public class BasicSessionTest extends AbstractTest {
    
    @SuppressWarnings("unused")
    private void mockMethod() {
    }
    
    @SuppressWarnings("unused")
    @CloudSessionOpenMode(OpenMode.EXISTING)
    private void mockMethod2() {
    }
    
    @Test
    public void LifecycleListener_Invokes_Session() throws SecurityException, NoSuchMethodException {
        
        CloudSession session = createStrictMock(CloudSession.class);
        
        session.openSession(OpenMode.LAZY);
        session.closeSession();
        
        session.openSession(OpenMode.LAZY);
        session.closeSession();
        
        replay(session);
        
        CloudSessionLifecycleListener listener = 
                new CloudSessionLifecycleListener(session);
        
        listener.beforeInitialize();
        listener.beforePageScopeDeactivation();
        
        listener.beforeUpdate(null, this.getClass().getDeclaredMethod("mockMethod"), null);
        listener.beforePageScopeDeactivation();
        
        verify(session);
    }
    
    @Test
    public void LifecycleListener_Does_Not_Invoke_Session() throws SecurityException, NoSuchMethodException {
        
        CloudSession session = createStrictMock(CloudSession.class);
        
        session.openSession(OpenMode.LAZY);
        session.closeSession();
        
        session.openSession(OpenMode.EXISTING);
        session.closeSession();
        
        replay(session);
        
        CloudSessionLifecycleListener listener = 
                new CloudSessionLifecycleListener(session);
        
        listener.beforeInitialize();
        listener.beforePageScopeDeactivation();
        
        listener.beforeUpdate(null,  this.getClass().getDeclaredMethod("mockMethod2"), null);
        listener.beforePageScopeDeactivation();
        
        verify(session);
    }
}
