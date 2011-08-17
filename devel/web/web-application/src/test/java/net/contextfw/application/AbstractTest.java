package net.contextfw.application;

import static org.easymock.EasyMock.createNiceMock;

public abstract class AbstractTest {

    protected <T> T createMock(Class<T> cl) {
        return createNiceMock(cl);
    }
}
