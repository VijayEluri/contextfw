package net.contextfw.web.application.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Tracker {

    private static final Logger LOG = LoggerFactory.getLogger(Tracker.class);
    
    private Tracker() {
    }
    
    public static void initialized(Class<?> implemented, Class<?> implementor) {
        LOG.info("Dependency initialized: {} ({})", 
                implemented.getSimpleName(), 
                implementor.getSimpleName());
    }
    
    public static void initialized(Class<?> implementor) {
        LOG.info("Dependency initialized: {}", implementor.getSimpleName());
    }
    
    public static void initialized(Object implementor) {
        initialized(implementor.getClass());
    }
}
