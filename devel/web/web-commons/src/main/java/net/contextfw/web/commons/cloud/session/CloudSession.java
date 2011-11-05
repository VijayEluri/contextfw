package net.contextfw.web.commons.cloud.session;

import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.configuration.SettableProperty;
import net.contextfw.web.application.configuration.TemporalProperty;

public interface CloudSession {
    
    SettableProperty<String> COOKIE_NAME = 
            Configuration.createProperty(String.class, 
                    CloudSession.class.getName() + ".cookieName");
    
    TemporalProperty MAX_INACTIVITY = 
            Configuration.createTemporalProperty(
                    CloudSession.class.getName() + ".maxInactivity");
    
    void set(String key, Object value);
    
    void set(Object value);
    
    <T> T get(String key, Class<T> type);
    
    <T> T get(Class<T> type);
    
    void unset(String key);
    
    void unset(Class<?> type);
    
    void openSession(OpenMode mode);
    
    void closeSession();
    
    void expireSession();
}