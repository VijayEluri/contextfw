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
    
    private volatile static Map<String, WebApplicationContext> contexts = new HashMap<String, WebApplicationContext>();

    public synchronized void refreshApplication(WebApplicationHandle handle) throws Exception {
        contexts.get(handle.getKey()).setTimestamp(System.currentTimeMillis());
    }

    public int getContextCount() {
        return contexts.size();
    }
    
    public synchronized WebApplicationContext getContext(WebApplicationHandle handle) {
        return contexts.get(handle.getKey());
    }

    public synchronized WebApplicationContext getContext(String handleKey) {
        return contexts.get(handleKey);
    }

    public synchronized void addContext(WebApplicationContext context) throws Exception {
        contexts.put(context.getHandle().getKey(), context);
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