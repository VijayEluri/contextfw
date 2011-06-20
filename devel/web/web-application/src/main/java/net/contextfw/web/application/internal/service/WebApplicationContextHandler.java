package net.contextfw.web.application.internal.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import net.contextfw.web.application.WebApplicationHandle;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.lifecycle.PageFlowFilter;

public class WebApplicationContextHandler {

    // private Logger logger = LoggerFactory.getLogger(WebApplicationContextHandler.class);
    
    private final long maxInactivity;
    
    public WebApplicationContextHandler(Configuration configuration, 
            PageFlowFilter pageFlowFilter) {
        maxInactivity = configuration.get(Configuration.MAX_INACTIVITY);
        this.pageFlowFilter = pageFlowFilter;
    }

    private final PageFlowFilter pageFlowFilter;
    
    private volatile static Map<WebApplicationHandle, WebApplicationContext> contexts = 
        new HashMap<WebApplicationHandle, WebApplicationContext>();

    public synchronized int refreshApplication(WebApplicationHandle handle) {
        WebApplicationContext context = contexts.get(handle);
        context.setExpires(System.currentTimeMillis() + maxInactivity);
        context.incrementUpdateCount();
        return context.getUpdateCount();
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

    public synchronized void addContext(WebApplicationContext context) {
        contexts.put(context.getHandle(), context);
    }

    public synchronized void removeExpiredApplications() {
        long timestamp = System.currentTimeMillis();
        
        Iterator<WebApplicationContext> iterator = contexts.values().iterator();

        while (iterator.hasNext()) {
            WebApplicationContext context = iterator.next();
            if (timestamp > context.getExpires()) {
                iterator.remove();
                pageFlowFilter.pageExpired(
                        getContextCount(),
                        context.getRemoteAddr(), 
                        context.getHandle().getKey());
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