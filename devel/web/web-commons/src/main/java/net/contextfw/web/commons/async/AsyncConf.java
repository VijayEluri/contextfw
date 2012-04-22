package net.contextfw.web.commons.async;

import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.configuration.SettableProperty;
import net.contextfw.web.commons.minifier.MinifierConf;

/**
 * Provides configuration for Async
 */
public final class AsyncConf {
    
    private AsyncConf() {
    }
    
    /**
     * The mode to be used in asynchronous functionality. Only Jetty and Tomcat are currently
     * supported 
     */
    public enum AsyncMode {
        JETTY, TOMCAT, OTHER, NONE
    }
    
    /**
     * Informs if web sockets can be enabled on server side
     */
    public static final SettableProperty<Boolean> WEB_SOCKET_ENABLED = 
            Configuration.createProperty(Boolean.class, 
                    MinifierConf.class.getName() + ".webSocketEnabled");
    
    public static final SettableProperty<AsyncMode> MODE = 
            Configuration.createProperty(AsyncMode.class, 
                    MinifierConf.class.getName() + ".mode");
    
    public static final SettableProperty<Boolean> MULTI_NODE_SUPPORT = 
            Configuration.createProperty(Boolean.class, 
                    MinifierConf.class.getName() + ".multiNodeSupport");
    
    public static final SettableProperty<Integer> WEBBIT_PORT = 
            Configuration.createProperty(Integer.class, 
                    MinifierConf.class.getName() + ".webbitPort");
    
    public static final SettableProperty<String> WEBBIT_HANDLER_PATH = 
            Configuration.createProperty(String.class, 
                    MinifierConf.class.getName() + ".webbitHandlerPath");

    
}
