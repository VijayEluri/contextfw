package net.contextfw.web.application.util;

public class Tracker {

    public static void initialized(Class<?> implemented, Class<?> implementor) {
        System.out.println("Dependency initialized: " 
                + implemented.getSimpleName() + "(" + implementor.getSimpleName() + ")");
    }
    
    public static void initialized(Class<?> implementor) {
        System.out.println("Dependency initialized: " 
                + implementor.getSimpleName());
    }
    
    public static void initialized(Object implementor) {
        initialized(implementor.getClass());
    }
}
