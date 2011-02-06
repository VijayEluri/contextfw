package net.contextfw.web.application.internal.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import net.contextfw.web.application.WebApplicationHandle;
import net.contextfw.web.application.conf.WebConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebApplicationContextHandler {

    private Logger logger = LoggerFactory.getLogger(WebApplicationContextHandler.class);
    
    public WebApplicationContextHandler(WebConfiguration configuration) {
        this.configuration = configuration;
    }

    private final WebConfiguration configuration;
    
    private volatile static Map<WebApplicationHandle, WebApplicationContext> contexts = 
        new HashMap<WebApplicationHandle, WebApplicationContext>();

    public synchronized void refreshApplication(WebApplicationHandle handle) throws Exception {
        contexts.get(handle).setTimestamp(System.currentTimeMillis());
    }

    public int getContextCount() {
        return contexts.size();
    }
    
    public synchronized WebApplicationContext getContext(WebApplicationHandle handle) {
        return contexts.get(handle);
    }

    public synchronized WebApplicationContext getContext(String handleKey) {
        return contexts.get(new WebApplicationHandle(handleKey));
    }

    public synchronized void addContext(WebApplicationContext context) throws Exception {
        contexts.put(context.getHandle(), context);
        refreshApplication(context.getHandle());
    }

    public synchronized void removeExpiredApplications() {
        long timestamp = System.currentTimeMillis();
        
        Iterator<WebApplicationContext> iterator = contexts.values().iterator();

        while (iterator.hasNext()) {
            WebApplicationContext context = iterator.next();
            if (timestamp - context.getTimestamp() > configuration.getMaxInactivity()) {
                iterator.remove();
                logger.debug("App removed: {}", context.getHandle());
            }
        }
    }

    public synchronized WebApplicationHandle createNewHandle() {
        String key = UUID.randomUUID().toString();

        while (contexts.containsKey(key)) {
            key = UUID.randomUUID().toString();
        }

        WebApplicationHandle handle = new WebApplicationHandle(key);

        return handle;
    }

    public synchronized void removeApplication(WebApplicationHandle handle) {
        contexts.remove(handle);
    }
}