package net.contextfw.web.commons.cloud.session;

import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.configuration.SettableProperty;
import net.contextfw.web.application.configuration.TemporalProperty;

/**
 * Provides a common interface for cloud based sessions.
 * 
 * @author marko.lavikainen@netkoti.fi
 */
public interface CloudSession {
    
    SettableProperty<String> COOKIE_NAME = 
            Configuration.createProperty(String.class, 
                    CloudSession.class.getName() + ".cookieName");
    
    TemporalProperty MAX_INACTIVITY = 
            Configuration.createTemporalProperty(
                    CloudSession.class.getName() + ".maxInactivity");
    
    /**
     * Sets a value with given key to the cloud session.
     * 
     * <p>
     *  The setting of the value is immediately seen by the session through get(), but
     *  setting the actual value to storage is delayed until session is closed.  
     * </p>
     * <p>
     *  If value has been removed or set earlier to different value, those changes will be
     *  overriden.
     * </p>
     * 
     * @param key
     *   The key to distinguish value
     * @param value
     *   The value to be set. If <code>null</code> is passed, the value is removed.
     */
    void set(String key, Object value);
    
    void set(String key, Object value, boolean nonCached);
    
    /**
     * Sets a value to the cloud session
     * 
     * <p>
     *  This method creates a key from values class name and dispatches it to 
     *  <code>set(String key, Object value)</code>. This method is nice shortcut for 
     *  objects that are stored as "singletons" to session.
     * </p>
     * 
     * @param value
     *   The value to be set
     */
    void set(Object value);
    
    void set(Object value, boolean nonCached);
 
    /**
     * 
     * @param key
     * @param type
     * @return
     */
    <T> T get(String key, Class<T> type);
    
    <T> T get(String key, Class<T> type, boolean nonCached);
    
    <T> T get(Class<T> type);
    
    <T> T get(Class<T> type, boolean nonCached);
    
    void remove(String key);
    
    void remove(Class<?> type);

    void remove(String key, boolean nonCached);
    
    void remove(Class<?> type, boolean nonCached);
    
    void openSession(OpenMode mode);
    
    void closeSession();
    
    void expireSession();
}