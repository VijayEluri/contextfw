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
    
    /**
     * Returns a synchronized version of sessioned data.
     * 
     * <p>
     *  With this method a specific get/set-cycle can be avoided. It is simply enough
     *  to get the object (and if not exists, use the default). With methods setChanged() 
     *  the state is pushed back to cloud during session close;
     * </p>
     * 
     * @param key
     * @param type
     * @param def
     * @param syncOnClose
     * @return
     */
    <T> T getSynched(String key, Class<T> type, ValueProvider<T> valueProvider);
    
    <T> T getSynched(Class<T> type, ValueProvider<T> valueProvider);
    
    void setChanged(Class<?> type);
    
    void setChanged(String key);
    
    void remove(String key);
    
    void remove(Class<?> type);
    
    void openSession(OpenMode mode);
    
    void closeSession();
    
    void expireSession();
}