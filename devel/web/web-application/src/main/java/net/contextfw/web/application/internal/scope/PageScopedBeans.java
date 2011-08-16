package net.contextfw.web.application.internal.scope;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Key;

public class PageScopedBeans {

    private static volatile ThreadLocal<PageScopedBeans> currentInstance = 
        new ThreadLocal<PageScopedBeans>();

    private Map<Key<?>, Object> beans = new HashMap<Key<?>, Object>();

    public Map<Key<?>, Object> getBeans() {
        return beans;
    }

    public static PageScopedBeans getCurrentInstance() {
        return currentInstance.get();
    }

    public void setAsCurrentInstance() {
        currentInstance.set(this);
    }

    public static void clearCurrentInstance() {
        currentInstance.set(null);
    }

    public static PageScopedBeans createNewInstance() {
        currentInstance.set(new PageScopedBeans());
        return currentInstance.get();
    }

    public <T> void seed(Class<T> clazz, T value) {
        seed(Key.get(clazz), value);
    }

    public <T> void seed(Key<T> key, T value) {
        beans.put(key, value);
    }
}