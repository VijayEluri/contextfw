package net.contextfw.web.application.util;


public final class ClassUtil {

    private ClassUtil() {}

    public static Class<?> getActualClass(Class<?> cl) {
        if (cl == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        Class<?> actual = cl;
        while (actual.getSimpleName().contains("EnhancerByGuice")) {
            actual = actual.getSuperclass();
        }
        return actual;
    }
}
