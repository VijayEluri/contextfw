package net.contextfw.web.application.internal.scope;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Key;

public class WebApplicationScopedBeans {

    private static volatile ThreadLocal<WebApplicationScopedBeans> currentInstance = new ThreadLocal<WebApplicationScopedBeans>();

    private Map<Key<?>, Object> beans = new HashMap<Key<?>, Object>();

    public Map<Key<?>, Object> getBeans() {
        return beans;
    }

    public static WebApplicationScopedBeans getCurrentInstance() {
        return currentInstance.get();
    }

    public void setAsCurrentInstance() {
        currentInstance.set(this);
    }

    public static void clearCurrentInstance() {
        currentInstance.set(null);
    }

    public static WebApplicationScopedBeans createNewInstance() {
        currentInstance.set(new WebApplicationScopedBeans());
        return currentInstance.get();
    }

    public <T> void seed(Class<T> clazz, T value) {
        seed(Key.get(clazz), value);
    }

    public <T> void seed(Key<T> key, T value) {
        beans.put(key, value);
    }
}