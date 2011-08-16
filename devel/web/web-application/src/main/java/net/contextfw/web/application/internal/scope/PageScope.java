package net.contextfw.web.application.internal.scope;

import java.util.Map;

import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;

public class PageScope implements Scope {

    private static final Provider<Object> SEEDED_KEY_PROVIDER = new Provider<Object>() {
        public Object get() {
            throw new IllegalStateException("If you got here then it means that"
                    + " your code asked for scoped object which should have been"
                    + " explicitly seeded in this scope by calling" + " SimpleScope.seed(), " +
                      "but was not.");
        }
    };

    public void enter() {
    }

    public void exit() {
        getScopedBeans().clear();
    }

    public <T> void seed(Key<T> key, T value) {
        getScopedBeans().put(key, value);
    }

    public <T> void seed(Class<T> clazz, T value) {
        seed(Key.get(clazz), value);
    }

    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {
            public T get() {
                Map<Key<?>, Object> scopedBeans = getScopedBeans();

                @SuppressWarnings("unchecked")
                T current = (T) scopedBeans.get(key);
                if (current == null && !scopedBeans.containsKey(key)) {
                    current = unscoped.get();
                    scopedBeans.put(key, current);
                }
                return current;
            }
        };
    }

    private <T> Map<Key<?>, Object> getScopedBeans() {
        try {
            Map<Key<?>, Object> scopedObjects = PageScopedBeans.getCurrentInstance().getBeans();
            if (scopedObjects == null) {
                throw new OutOfScopeException("PageScope does not exist");
            }
            return scopedObjects;
        }
        catch (Exception e) {
            throw new OutOfScopeException("PageScope does not exist", e);
        }
    }

    /**
     * Returns a provider that always throws exception complaining that the
     * object in question must be seeded before it can be injected.
     * 
     * @return typed provider
     */
    @SuppressWarnings( { "unchecked" })
    public static <T> Provider<T> seededKeyProvider() {
        return (Provider<T>) SEEDED_KEY_PROVIDER;
    }
}