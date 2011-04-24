package net.contextfw.web.application.internal.service;

import net.contextfw.web.application.HttpContext;
import net.contextfw.web.application.WebApplicationHandle;
import net.contextfw.web.application.internal.scope.WebApplicationScopedBeans;

public class WebApplicationContext {

    private WebApplicationHandle handle;

    private HttpContext httpContext;
    
    private final String remoteAddr;
    
    private int updateCount = 0;

    public int getUpdateCount() {
        return updateCount;
    }
    
    public void incrementUpdateCount() {
        updateCount++;
    }
    
    public WebApplicationContext(HttpContext httpContext, 
            String remoteAddr,
            long expires, 
            WebApplicationHandle handle, 
            WebApplicationScopedBeans beans) {
        super();
        this.handle = handle;
        this.beans = beans;
        this.httpContext = httpContext;
        this.remoteAddr = remoteAddr;
        this.expires = expires;
    }

    private long expires = 0;

    public WebApplicationScopedBeans getBeans() {
        return beans;
    }

    public void setBeans(WebApplicationScopedBeans beans) {
        this.beans = beans;
    }

    public WebApplication getApplication() {
        return application;
    }

    public void setApplication(WebApplication application) {
        this.application = application;
    }

    public void setHandle(WebApplicationHandle handle) {
        this.handle = handle;
    }

    public WebApplicationHandle getHandle() {
        return handle;
    }

    public HttpContext getHttpContext() {
        return httpContext;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    public long getExpires() {
        return expires;
    }

    private WebApplicationScopedBeans beans;

    private WebApplication application;
}