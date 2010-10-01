package net.contextfw.web.application.internal.service;

import net.contextfw.web.application.HttpContext;
import net.contextfw.web.application.WebApplicationHandle;
import net.contextfw.web.application.internal.scope.WebApplicationScopedBeans;

public class WebApplicationContext {

    private WebApplicationHandle handle;

    private HttpContext httpContext;

    public WebApplicationContext(HttpContext httpContext, WebApplicationHandle handle, WebApplicationScopedBeans beans) {
        super();
        this.handle = handle;
        this.beans = beans;
        this.httpContext = httpContext;
    }

    private long timestamp = 0;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

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

    private WebApplicationScopedBeans beans;

    private WebApplication application;
}