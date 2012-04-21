package net.contextfw.web.commons.async.internal.comet;

import net.contextfw.web.application.PageHandle;

public class BatonProvider {

    private Object[] batons = new Object[256];
    
    public BatonProvider() {
        for (int i = 0; i < 256; i++) {
            batons[i] = new Object();
        }
    }
    
    public Object get(PageHandle handle) {
        return batons[((handle.hashCode() % 256) + 256) % 256];
    }
    
}
