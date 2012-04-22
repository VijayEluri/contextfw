package net.contextfw.web.commons.cloud.session;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import net.contextfw.web.application.PageContext;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.configuration.SettableProperty;
import net.contextfw.web.commons.cloud.binding.CloudDatabase;
import net.contextfw.web.commons.cloud.internal.mongo.MongoBase;
import net.contextfw.web.commons.cloud.internal.serializer.Serializer;
import net.contextfw.web.commons.cloud.internal.session.CloudSessionHolder;

import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Provides MongoDB-based cloud session handling.
 * 
 * <p>
 *  This session handler requires that there exists a <code>DB</code>-instance that is bound to 
 *  DI-container with annotation @CloudDatabase
 * </p>
 * 
 */
@Singleton
public class MongoCloudSession extends MongoBase implements CloudSession {

    private static final String KEY_CANNOT_BE_EMPTY_OR_NULL = "Key cannot be empty or null.";

    private static final String TYPE_CANNOT_BE_EMPTY_OR_NULL = "Type cannot be empty or null.";

    private static final String NO_SESSION = "Cannot open session in EXISTING-mode! " +
        	  "Session has not been initialized";
    
    private static final String SESSION_NOT_OPEN = "Session is not open.";

    private static class LocalData {
        Map<String, Object> cache = new HashMap<String, Object>();
        Set<String> changed = new HashSet<String>();
        Set<String> removed = new HashSet<String>();
        OpenMode openMode;
    }
    
    private final Provider<CloudSessionHolder> holderProvider;
    
    private final ThreadLocal<LocalData> localDataHolder = new ThreadLocal<LocalData>() {
        protected LocalData initialValue() {
            return new LocalData();
        }
    };
    
    private final Serializer serializer;
    
    public static final SettableProperty<String> COLLECTION_NAME = 
            Configuration.createProperty(String.class, 
                    MongoCloudSession.class.getCanonicalName() + ".collectionName");
    
    private final Provider<PageContext> httpContext;
    private static final long HALF_HOUR = 30*60*1000;
    
    private final String cookieName;
    private final String sessionCollection;
    private final long maxInactivity;
    
    @Inject
    public MongoCloudSession(@CloudDatabase DB db, 
                             Configuration configuration, 
                             Provider<PageContext> httpContext,
                             Provider<CloudSessionHolder> holderProvider,
                             Serializer serializer) {
        super(db, configuration.get(Configuration.REMOVAL_SCHEDULE_PERIOD));
        
        this.httpContext = httpContext;
        cookieName = configuration.getOrElse(CloudSession.COOKIE_NAME, "cloudSession");
        sessionCollection = configuration.getOrElse(COLLECTION_NAME, "session");
        this.maxInactivity = configuration.getOrElse(CloudSession.MAX_INACTIVITY, HALF_HOUR);
        this.serializer = serializer;
        this.holderProvider = holderProvider;
        setIndexes(getCollection());
    }
    
    @Override
    public void set(final String key, final Object value, boolean nonCached) {
        
        assertSessionIsUsable();
        
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException(KEY_CANNOT_BE_EMPTY_OR_NULL);
        }
        
        final String handle = getSessionHandle(localDataHolder.get().openMode != OpenMode.EXISTING, true);
        
        if (handle != null) {
            
            if (value == null) {
                unset(handle, key, nonCached);
            }
            
            LocalData data = localDataHolder.get();
            
            data.removed.remove(key);
            data.changed.add(key);
            data.cache.put(key, value);
            
            if (nonCached) {
                DBObject query = o(KEY_HANDLE, handle);
                DBObject update = o(SET, o(key, serializer.serialize(value)));
                getCollection().update(query, update);
            }
        }
    }

    private boolean isSessionValid(String handle) {
        DBObject query = b()
                .add(KEY_HANDLE, handle)
                .push(KEY_VALID_THROUGH)
                .add(GTE, System.currentTimeMillis()).get();
        
        return getCollection().count(query) > 0;
    }

    private String createSession() {
        String handle = UUID.randomUUID().toString();
        DBObject session = new BasicDBObject();
        session.put(KEY_HANDLE, handle);
        session.put(KEY_LOCKED, false);
        session.put(KEY_VALID_THROUGH, System.currentTimeMillis() + maxInactivity);
        getCollection().insert(session);
        return handle;
    }
    
    private String getSessionHandle(boolean create, boolean assignCookie) {

        CloudSessionHolder holder = this.holderProvider.get();
        
        if (holder.getHandle() != null && holder.isOpen()) {
            return holder.getHandle();
        }
        
        if (holder.getHandle() == null) {
            holder.setHandle(findHandleFromCookie());
        }
        
        if (holder.getHandle() != null && !isSessionValid(holder.getHandle())) {
            holder.setHandle(null);
        }
        
        if (holder.getHandle() == null && create) {
            if (httpContext.get().getResponse() != null) {
                holder.setHandle(createSession());
                if (assignCookie) {
                    setSessionCookie(holder.getHandle(), false);
                }
            } else {
                throw new NoSessionException(
                        "Cannot create new session! " + 
                        "HttpResponse not bound");
            }
        }
        
        return holder.getHandle();
    }
    
    private String typeToKey(Class<?> type) {
        return type.getName().replaceAll("\\.", "");
    }
    
    @Override
    public void set(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null.");
        }
        set(typeToKey(value.getClass()), value, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(final String key, Class<T> type, boolean nonCached) {
        
        assertSessionIsUsable();
        
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException(KEY_CANNOT_BE_EMPTY_OR_NULL);
        }
        if (type == null) {
            throw new IllegalArgumentException(TYPE_CANNOT_BE_EMPTY_OR_NULL);
        }
        
        LocalData localData = getLocalData();
        
        if (localData.removed.contains(key)) {
            return null;
        } else if (!nonCached && localData.cache.containsKey(key)) {
            return (T) localData.cache.get(key);
        }
        
        final String handle = getSessionHandle(false, false);
        
        if (handle != null) {
            DBObject query = o(KEY_HANDLE, handle);
            DBObject field = o(key, 1);
            DBObject obj = getCollection().findOne(query, field);
            byte[] data = (byte[]) (obj == null ? null : obj.get(key));
            T rv = data == null ? null : (T) serializer.unserialize(data);
            localData.cache.put(key, rv);
            return rv;
        } else {
            return null;
        }
    }

    private LocalData getLocalData() {
        return localDataHolder.get();
    }

    @Override
    public <T> T get(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException(TYPE_CANNOT_BE_EMPTY_OR_NULL);
        }
        return get(typeToKey(type), type, false);
    }
    
    private void assertSessionIsUsable() {
        CloudSessionHolder holder = holderProvider.get();
        if (!holder.isOpen()) {
            throw new NoSessionException(SESSION_NOT_OPEN);
        }
    }

    @Override
    public void remove(String key, boolean nonCached) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException(KEY_CANNOT_BE_EMPTY_OR_NULL);
        }
        
        String handle = getSessionHandle(false, false);
        if (handle != null) {
            unset(handle, key, nonCached);
        }
    }
    
    private void unset(final String handle, final String key, boolean nonCached) {
        assertSessionIsUsable();
        LocalData localData = this.localDataHolder.get();
        localData.cache.remove(key);
        localData.changed.remove(key);
        localData.removed.add(key);
        
        if (nonCached) {
            DBObject query = o(KEY_HANDLE, handle);
            DBObject update = o(UNSET, o(key, 1));
            getCollection().update(query, update);
        }
    }

    @Override
    public void remove(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException(TYPE_CANNOT_BE_EMPTY_OR_NULL);
        }
        remove(typeToKey(type));
    }

    @Override
    public void expireSession() {
        String handle = getSessionHandle(false, false);
        localDataHolder.remove();
        if (handle != null) {
            setSessionCookie(handle, true);
            removeSession(handle);
        }
    }

    private void removeSession(String handle) {
        getCollection().remove(o(KEY_HANDLE, handle));
    }
    
    private String findHandleFromCookie() {
        HttpServletRequest request = httpContext.get().getRequest();
        if (request != null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(cookieName)) {
                        return cookie.getValue();
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public void openSession(OpenMode mode) {
        localDataHolder.remove();
        localDataHolder.get().openMode = mode;
        removeExpiredSessions();
        CloudSessionHolder holder = this.holderProvider.get();
        
        if (httpContext.get().getRequest() == null && mode != OpenMode.EXISTING) {
            throw new NoSessionException(
                    "Cannot open session in " + mode + "-mode! " +
                    "No request bound to PageContext. Use EXISTING-mode instead");
        }
        
        String handle = getSessionHandle(mode == OpenMode.EAGER, false);
        
        if (holder.getHandle() == null && mode == OpenMode.EXISTING) {
            throw new NoSessionException(NO_SESSION);
        }

        holder.setOpen(true);
        
        if (handle != null && mode != OpenMode.EXISTING) {
            setSessionCookie(handle, false);
            refreshSession(handle);
        }
        
        if (holder.getHandle() == null && mode == OpenMode.EXISTING) {
            throw new NoSessionException(NO_SESSION);
        }
    }

    private void setSessionCookie(String handle, boolean remove) {
        if (httpContext.get().getResponse() != null) {
            Cookie cookie = new Cookie(cookieName, handle);
            cookie.setPath("/");
            cookie.setMaxAge(remove ? 0 : (int) (maxInactivity/1000));
            httpContext.get().getResponse().addCookie(cookie);
        }
    }

    private void refreshSession(String handle) {
        getCollection().update(o(KEY_HANDLE, handle),
                o(SET, 
                        o(KEY_VALID_THROUGH, 
                        System.currentTimeMillis() + maxInactivity)));
    }
    
    @Override
    protected final DBCollection getCollection() {
        return getDb().getCollection(sessionCollection);
    }
    
    private void removeExpiredSessions() {
        removeExpiredObjects();
    }

    @Override
    public void closeSession() {
        
        final String handle = getSessionHandle(localDataHolder.get().openMode != OpenMode.EXISTING, false);
        final LocalData ld = localDataHolder.get();
        
        if (!ld.changed.isEmpty() || !ld.removed.isEmpty()) {
        
            DBObject query = o(KEY_HANDLE, handle);
            
            BasicDBObjectBuilder update = b();
            if (!ld.removed.isEmpty()) {
                update.push(UNSET);
                for (String _ : ld.removed) {
                    update.add(_, 1);
                }
                update.pop();
            }
            if (!ld.changed.isEmpty()) {
                update.push(SET);
                for (String _ : ld.changed) {
                    update.add(_, serializer.serialize(ld.cache.get(_)));
                }
                update.pop();
            }
            
            getCollection().update(query, update.get());
        }
        
        localDataHolder.remove();
        holderProvider.get().setOpen(false);
    }

    @Override
    public void set(String key, Object value) {
        set(key, value, false);
    }

    @Override
    public void set(Object value, boolean nonCached) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null.");
        }
        set(typeToKey(value.getClass()), value, nonCached);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        return get(key, type, false);
    }

    @Override
    public <T> T get(Class<T> type, boolean nonCached) {
        if (type == null) {
            throw new IllegalArgumentException(TYPE_CANNOT_BE_EMPTY_OR_NULL);
        }
        return get(typeToKey(type), type, nonCached);
    }

    @Override
    public void remove(String key) {
        remove(key, false);
    }

    @Override
    public void remove(Class<?> type, boolean nonCached) {
        if (type == null) {
            throw new IllegalArgumentException(TYPE_CANNOT_BE_EMPTY_OR_NULL);
        }
        remove(typeToKey(type), nonCached);
    }
}