package net.contextfw.web.application.internal.util;

import com.google.inject.Injector;

public final class Utils {

    private Utils() {}
    
    @SuppressWarnings("unchecked")
    public static <T> T toInstance(Object obj, Injector injector) {
        if (obj instanceof Class<?>) {
            return (T) injector.getInstance((Class<T>) obj);
        } else {
            return (T) obj;
        }
    }
    
}
