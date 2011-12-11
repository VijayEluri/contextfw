package net.contextfw.web.commons.cloud.session;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import net.contextfw.web.application.HttpContext;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.configuration.SettableProperty;
import net.contextfw.web.commons.cloud.binding.CloudDatabase;
import net.contextfw.web.commons.cloud.mongo.MongoBase;
import net.contextfw.web.commons.cloud.mongo.MongoExecution;
import net.contextfw.web.commons.cloud.serializer.Serializer;

import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

@Singleton
public class MongoCloudSession extends MongoBase implements CloudSession {

    private static final String NO_SESSION = "Cannot open session in EXISTING-mode! " +
        	  "Session has not been initialized";
    
    private static final String SESSION_NOT_OPEN = "Session is not open.";

    private static class LocalData {
        Map<String, Object> cache = new HashMap<String, Object>();
        Set<String> changed = new HashSet<String>();
        OpenMode openMode;
    }
    
    private final Provider<CloudSessionHolder> holderProvider;
    
    private final ThreadLocal<LocalData> localData = new ThreadLocal<LocalData>() {
        protected LocalData initialValue() {
            return new LocalData();
        }
    };
    
    private final Serializer serializer;
    
    public static final SettableProperty<String> COLLECTION_NAME = 
            Configuration.createProperty(String.class, 
                    MongoCloudSession.class.getCanonicalName() + ".collectionName");
    
    private final Provider<HttpContext> httpContext;
    private static final long HALF_HOUR = 30*60*1000;
    
    private final String cookieName;
    private final String sessionCollection;
    private final long maxInactivity;
    
    @Inject
    public MongoCloudSession(@CloudDatabase DB db, 
                             Configuration configuration, 
                             Provider<HttpContext> httpContext,
                             Provider<CloudSessionHolder> holderProvider,
                             Serializer serializer) {
        super(db, configuration.get(Configuration.REMOVAL_SCHEDULE_PERIOD));
        
        this.httpContext = httpContext;
        cookieName = configuration.getOrElse(CloudSession.COOKIE_NAME, "cloudSession");
        sessionCollection = configuration.getOrElse(COLLECTION_NAME, "session");
        this.maxInactivity = configuration.getOrElse(CloudSession.MAX_INACTIVITY, HALF_HOUR);
        this.serializer = serializer;
        this.holderProvider = holderProvider;
        setIndexes(getSessionCollection());
    }
    
    @Override
    public void set(final String key, final Object value) {
        
        assertSessionIsUsable();
        
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be empty or null.");
        }
        
        final String handle = getSessionHandle(localData.get().openMode != OpenMode.EXISTING, true);
        
        if (handle != null) {
            
            if (value == null) {
                unset(handle, key);
            }
            DBObject fields = o(key, 1);
            executeSynchronized(getSessionCollection(), 
                                handle, 
                                fields, 
                                null,
                                false,
                                false,
                    new MongoExecution<Void>() {
                        public Void execute(DBObject object) {
                            DBObject query = o(KEY_HANDLE, handle);
                            DBObject update = o(key, serializer.serialize(value));
                            closeExclusive(getSessionCollection(), query, update);
                            return null;
                        }
                    });
        }
    }

    private boolean isSessionValid(String handle) {
        DBObject query = b()
                .add(KEY_HANDLE, handle)
                .push(KEY_VALID_THROUGH)
                .add("$gte", System.currentTimeMillis()).get();
        
        return getSessionCollection().count(query) > 0;
    }

    private String createSession() {
        String handle = UUID.randomUUID().toString();
        DBObject session = new BasicDBObject();
        session.put(KEY_HANDLE, handle);
        session.put(KEY_LOCKED, false);
        session.put(KEY_VALID_THROUGH, System.currentTimeMillis() + maxInactivity);
        getSessionCollection().insert(session);
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
        set(typeToKey(value.getClass()), value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(final String key, Class<T> type) {
        
        assertSessionIsUsable();
        
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be empty or null.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be empty or null.");
        }
        
        final String handle = getSessionHandle(false, false);
        
        if (handle != null) {
            DBObject fields = o(key, 1);
            byte[] data = executeSynchronized(getSessionCollection(), 
                                              handle, 
                                              fields,
                                              null,
                                              false,
                                              false,
                    new MongoExecution<byte[]>() {
                        public byte[] execute(DBObject object) {
                            DBObject query = o(KEY_HANDLE, handle);
                            DBObject update = o("$set", o(KEY_LOCKED, false));
                            getSessionCollection().update(query, update);
                            return (byte[]) object.get(key);
                        }
                    });
            return data == null ? null : (T) serializer.unserialize(data);
        } else {
            return null;
        }
    }

    @Override
    public <T> T get(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be empty or null.");
        }
        return get(typeToKey(type), type);
    }
    
    private void assertSessionIsUsable() {
        CloudSessionHolder holder = holderProvider.get();
        if (!holder.isOpen()) {
            throw new NoSessionException(SESSION_NOT_OPEN);
        }
    }

    @Override
    public void remove(String key) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be empty or null.");
        }
        
        String handle = getSessionHandle(false, false);
        if (handle != null) {
            unset(handle, key);
        }
    }
    
    private void unset(final String handle, final String key) {
        assertSessionIsUsable();
        LocalData localData = this.localData.get();
        localData.cache.remove(key);
        localData.changed.remove(key);
        DBObject fields = o(key, 1);
        executeSynchronized(getSessionCollection(), 
                            handle, 
                            fields, 
                            null,
                            false,
                            false,
                new MongoExecution<Void>() {
                    public Void execute(DBObject object) {
                            DBObject query = o(KEY_HANDLE, handle);
                            DBObject update = o("$unset", o(key, 1));
                            update.put("$set", o(KEY_LOCKED, false));
                            getSessionCollection().update(query, update);
                            return null;
                        }
                    });
    }

    @Override
    public void remove(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be empty or null.");
        }
        remove(typeToKey(type));
    }

    @Override
    public void expireSession() {
        String handle = getSessionHandle(false, false);
        if (handle != null) {
            setSessionCookie(handle, true);
            removeSession(handle);
        }
    }

    private void removeSession(String handle) {
        getSessionCollection().remove(o(KEY_HANDLE, handle));
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
        localData.remove();
        localData.get().openMode = mode;
        removeExpiredSessions();
        CloudSessionHolder holder = this.holderProvider.get();
        
        if (httpContext.get().getRequest() == null && mode != OpenMode.EXISTING) {
            throw new NoSessionException(
                    "Cannot open session in " + mode + "-mode! " +
                    "No request bound to HttpContext. Use EXISTING-mode instead");
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
        getSessionCollection().update(o(KEY_HANDLE, handle),
                o("$set", 
                        o(KEY_VALID_THROUGH, 
                        System.currentTimeMillis() + maxInactivity)));
    }
    
    private DBCollection getSessionCollection() {
        return getDb().getCollection(sessionCollection);
    }
    
    private void removeExpiredSessions() {
        removeExpiredObjects(getSessionCollection());
    }

    @Override
    public void closeSession() {
        LocalData ld = localData.get();
        for (Entry<String, Object> cached : ld.cache.entrySet()) {
            if (ld.changed.contains(cached.getKey())) {
                set(cached.getKey(), cached.getValue());
            }
        }
        localData.remove();
        holderProvider.get().setOpen(false);
    }

    @Override
    public <T> T getSynched(String key, Class<T> type, ValueProvider<T> provider) {
        
        Map<String, Object> cache = localData.get().cache;
        
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be empty or null.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null.");
        }
        if (provider == null) {
            throw new IllegalArgumentException("ValueProvider cannot be null.");
        }
        
        @SuppressWarnings("unchecked")
        T value = (T) cache.get(key);
        
        if (value == null) {
            value = get(key, type);
            if (value == null) {
                value = provider.initialValue();
            }
            if (value != null) {
                cache.put(key, value);
            }
        }
        return value;
    }

    @Override
    public <T> T getSynched(Class<T> type, ValueProvider<T> provider) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be empty or null.");
        }
        return getSynched(typeToKey(type), type, provider);
    }

    @Override
    public void setChanged(Class<?> type) {
        setChanged(typeToKey(type));
    }

    @Override
    public void setChanged(String key) {
        localData.get().changed.add(key);
    }
}
