package net.contextfw.web.application.internal.service;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public abstract class AbstractHandler {

    private final boolean proxied;
    
    private static final int MAX_LENGTH = 16;
    
    protected AbstractHandler(boolean proxied) {
        this.proxied = proxied;
    }
    
    protected String getRemoteAddr(HttpServletRequest request) {
        if (proxied) {
            String proxy = StringUtils.trimToEmpty(request.getHeader("X-Forwarded-For"));
            int length = proxy.length();
            if (length > MAX_LENGTH) {
                return proxy.substring(length - MAX_LENGTH, length);
            } else {
                return proxy;
            }
        } else {
            return request.getRemoteAddr();
        }
    }
}
