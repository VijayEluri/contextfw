package net.contextfw.web.application.internal.page;

import net.contextfw.web.application.WebApplicationHandle;
import net.contextfw.web.application.internal.service.WebApplication;

import com.google.inject.Key;

public interface WebApplicationPage {

    <T> T setBean(Key<T> key, T value);
    
    <T> T getBean(Key<T> key);
    
    String getRemoteAddr();
    
    WebApplicationHandle getHandle();
    
    WebApplication getWebApplication();
    
    void setWebApplication(WebApplication application);
    
    int refresh(long expires);
    
    boolean isExpired(long now);
    
    
}
