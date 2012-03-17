package net.contextfw.web.commons.cloud.session;

import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.configuration.SettableProperty;
import net.contextfw.web.application.configuration.TemporalProperty;

/**
 * A common interface for cloud based sessions.
 * 
 * <p>
 * Cloud session is a session handling, where session data is kept serialized on
 * database and allows multiple nodes to access same data in common way.
 * </p>
 * <p>
 * Session is a simple key/value-store and supports basic CRUD-operations.
 * Object are serialized and deserialized, so all kind of structures can be
 * stored in transparent way.
 * </p>
 * <p>
 * When values are fetched or stored, operations are cached by default and are
 * synced to database when session is closed. In this way, database connections
 * are minimized. For all CRUD-methods there exists also a non-cached variant if
 * that is needed.
 * </p>
 * 
 * <h2>Opening and closing session</h2>
 * 
 * It is best to handle opening and closing through LifecycleListener and there
 * exists a ready made listener CloudSessionLifecycleListener for it.
 * 
 * By default session is opened in lazy-mode where session cookie and data
 * structures are created only when needed. This minifies unnecessary session
 * creations.
 * 
 * <h2>Opening session in existing mode</h2>
 * 
 * Sometimes it is necessary to open session in such mode that it must exist in
 * order to be effective. This is important especially in applications that poll
 * server frequently. In essence frequent polling may prevent session
 * expiration.
 * 
 * The CloudSessionLifecycleListener is configured in such way that if remote
 * method is also annotated with
 * <code>@CloudSessionOpenMode(OpenMode.EXISTING)</code>, an existing session is
 * required and session expiration is not altered.
 * 
 * @author marko.lavikainen@netkoti.fi
 */
public interface CloudSession {

    /**
     * Defines the the name of the cookie used to hold session handle.
     */
    SettableProperty<String> COOKIE_NAME =
            Configuration.createProperty(String.class,
                    CloudSession.class.getName() + ".cookieName");

    /**
     * Defines the maximum inactivity for session before it is expired.
     */
    TemporalProperty MAX_INACTIVITY =
            Configuration.createTemporalProperty(
                    CloudSession.class.getName() + ".maxInactivity");

    /**
     * Closes session and syncs all cached data to database.
     */
    void closeSession();

    /**
     * Expires session and removes possible cookies and database structures.
     */
    void expireSession();

    /**
     * Fetches a value for given type.
     * 
     * <p>
     *  If type has been fetched before it is taken from local cache.
     * </p>
     * @param type
     *   The type to be fetched.
     * @return
     *   Corresponding value or <code>null</code> if value does not exist.
     */
    <T> T get(Class<T> type);

    /**
     * 
     * @param type
     * @param nonCached
     * @return
     * 
     *      * 
     * <p>
     *  If type has been fetched before it is taken from local cache.
     * </p>
     * @param type
     *   The type to be fetched.
     * @return
     *   Corresponding value or <code>null</code> if value does not exist.
     * 
     */
    <T> T get(Class<T> type, boolean nonCached);

    /**
     * 
     * @param key
     * @param type
     * @return
     */
    <T> T get(String key, Class<T> type);

    <T> T get(String key, Class<T> type, boolean nonCached);

    void openSession(OpenMode mode);

    void remove(Class<?> type);

    void remove(Class<?> type, boolean nonCached);

    void remove(String key);

    void remove(String key, boolean nonCached);

    /**
     * Sets a value to the cloud session
     * 
     * <p>
     * This method creates a key from values class name and dispatches it to
     * <code>set(String key, Object value)</code>. This method is nice shortcut
     * for objects that are stored as "singletons" to session.
     * </p>
     * 
     * @param value
     *            The value to be set
     */
    void set(Object value);

    void set(Object value, boolean nonCached);

    /**
     * Sets a value with given key to the cloud session.
     * 
     * <p>
     * The setting of the value is immediately seen by the session through
     * get(), but setting the actual value to storage is delayed until session
     * is closed.
     * </p>
     * <p>
     * If value has been removed or set earlier to different value, those
     * changes will be overriden.
     * </p>
     * 
     * @param key
     *            The key to distinguish value
     * @param value
     *            The value to be set. If <code>null</code> is passed, the value
     *            is removed.
     */
    void set(String key, Object value);

    void set(String key, Object value, boolean nonCached);
}