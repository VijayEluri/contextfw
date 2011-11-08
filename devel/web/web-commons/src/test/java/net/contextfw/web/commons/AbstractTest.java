package net.contextfw.web.commons;

import static org.easymock.EasyMock.createNiceMock;
import org.easymock.EasyMock;

public abstract class AbstractTest {

    protected <T> T createMock(Class<T> cl) {
        return createNiceMock(cl);
    }
    
    protected <T> T createStrictMock(Class<T> cl) {
        return EasyMock.createStrictMock(cl);
    }
    
    protected void sleep(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
        }
    }
}