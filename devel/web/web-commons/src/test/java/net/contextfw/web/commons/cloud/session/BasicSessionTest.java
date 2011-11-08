package net.contextfw.web.commons.cloud.session;

import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import net.contextfw.web.commons.AbstractTest;

import org.junit.Test;

public class BasicSessionTest extends AbstractTest {
    
    @Test
    public void LifecycleListener_Invokes_Session() {
        
        CloudSession session = createStrictMock(CloudSession.class);
        
        session.openSession(OpenMode.LAZY);
        session.closeSession();
        
        session.openSession(OpenMode.LAZY);
        session.closeSession();
        
        replay(session);
        
        CloudSessionLifecycleListener listener = 
                new CloudSessionLifecycleListener(session);
        
        listener.beforeInitialize();
        listener.afterInitialize();
        
        listener.beforeUpdate(null,  null, null);
        listener.afterUpdate(null,  null, null);
        
        verify(session);
    }
}
