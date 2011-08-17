package net.contextfw.web.application.internal.page;

import java.util.HashMap;
import java.util.Map;

import net.contextfw.web.application.WebApplicationHandle;

import com.google.inject.Key;

public class WebApplicationPageImpl implements WebApplicationPage {

    private static final Key<WebApplicationHandle> HANDLE_KEY = Key.get(WebApplicationHandle.class);

    private Map<Key<?>, Object> beans = new HashMap<Key<?>, Object>();

    private final String remoteAddr;
    
    private long expires = 0;
    
    private int updateCount = 0;
    
    public WebApplicationPageImpl(WebApplicationHandle handle, String remoteAddr) {
        this.remoteAddr = remoteAddr;
        beans.put(HANDLE_KEY, handle);
    }
    
    
    @Override
    public <T> T setBean(Key<T> key, T value) {
        beans.put(key, value);
        return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Key<T> key) {
        return (T) beans.get(key);
    }

    @Override
    public String getRemoteAddr() {
        return remoteAddr;
    }

    @Override
    public int refresh(long expires) {
        this.expires = expires;
        return ++updateCount;
    }

    @Override
    public boolean isExpired(long now) {
        return now > expires;
    }


    @Override
    public WebApplicationHandle getHandle() {
        return (WebApplicationHandle) beans.get(HANDLE_KEY);
    }
    
}