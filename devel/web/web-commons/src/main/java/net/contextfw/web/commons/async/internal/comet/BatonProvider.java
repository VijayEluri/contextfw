package net.contextfw.web.commons.async.internal.comet;

import net.contextfw.web.application.PageHandle;

public class BatonProvider {

    private static final int BATON_COUNT = 256;
    
    private Object[] batons = new Object[BATON_COUNT];
    
    public BatonProvider() {
        for (int i = 0; i < 256; i++) {
            batons[i] = new Object();
        }
    }
    
    public Object get(PageHandle handle) {
        return batons[((handle.hashCode() % BATON_COUNT) + BATON_COUNT) % BATON_COUNT];
    }
    
}
