package net.contextfw.web.application.scope;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import net.contextfw.web.application.PageHandle;
import net.contextfw.web.application.WebApplication;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.util.Tracker;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultWebApplicationStorage implements WebApplicationStorage {

    private Logger logger = LoggerFactory.getLogger(DefaultWebApplicationStorage.class);

    private final Map<PageHandle, Holder> pages = 
            new HashMap<PageHandle, Holder>();
    
    private static final class Holder {
        private long validThrough;
        private final String remoteAddr;
        private final WebApplication application;
        @SuppressWarnings("PMD.UnusedPrivateField") // For some reason PMD reports this as unused
        private final Map<String, Object> largeObjects = new HashMap<String, Object>();
        
        private Holder(WebApplication application, 
                             String remoteAddr, 
                             long validThrough) {
            this.application = application;
            this.remoteAddr = remoteAddr;
            this.validThrough = validThrough;
        }
    }
    
    @Inject
    public DefaultWebApplicationStorage(Configuration configuration) {
        
        Timer timer = new Timer(true);
        logger.info("Starting scheduled removal for expired web applications");

        timer.schedule(new TimerTask() {
            public void run() {
                removeExpiredPages();
            }
        }, configuration.get(Configuration.REMOVAL_SCHEDULE_PERIOD),
                configuration.get(Configuration.REMOVAL_SCHEDULE_PERIOD));
        
        Tracker.initialized(this);
    }
    
    @Override
    public void initialize(WebApplication application, 
                           String remoteAddr,
                           long validThrough, 
                           ScopedWebApplicationExecution execution) {
        
        PageHandle handle = createHandle();
        application.setHandle(handle);
        Holder holder = new Holder(application, remoteAddr, validThrough);
        synchronized(this) {
            pages.put(handle, holder);
        }
        synchronized (holder) {
            execution.execute(holder.application);
        }
    }

    @Override
    public void update(PageHandle handle, 
                       String remoteAddr,
                       long validThrough,
                       ScopedWebApplicationExecution execution) {
        
        Holder holder = getHolder(handle, remoteAddr);
        
        if (holder != null) {
            synchronized (holder) {
                holder.validThrough = validThrough;
                execution.execute(holder.application);
            }
        } else {
            execution.execute(null);
        }
    }

    @Override
    public void refresh(PageHandle handle, 
                        String remoteAddr, 
                        long validThrough) {
        
        Holder holder = getHolder(handle, remoteAddr);
        if (holder != null) {
            holder.validThrough = validThrough;
        }
    }
    
    private Holder getHolder(PageHandle handle, String remoteAddr) {
        Holder holder;
        synchronized (this) {
            holder = pages.get(handle);    
        }
        long now = System.currentTimeMillis();
        if (holder != null
                && (remoteAddr == null || holder.remoteAddr.equals(remoteAddr)) 
                && holder.validThrough >= now) {
            return holder;
        } else {
            return null;
        }
    }

    @Override
    public synchronized void remove(PageHandle handle,
                                    String remoteAddr) {
        
        Holder holder = getHolder(handle, remoteAddr);
        if (holder != null) {
            pages.remove(handle);
            pageRemoved(handle, pages.size(), remoteAddr);
        }
    }

    protected void pageRemoved(PageHandle handle, int pageCount, String remoteAddr) {
        
    }
    
    protected void pageExpired(PageHandle handle, int pageCount, String remoteAddr) {
        
    }
    
    protected void throttle() {
        
    }
    
    private synchronized void removeExpiredPages() {

        long now = System.currentTimeMillis();

        try {
            Iterator<Entry<PageHandle, Holder>> iterator =
                pages.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<PageHandle, Holder> entry = iterator.next();
                if (entry.getValue().validThrough < now) {
                    pageExpired(entry.getKey(), pages.size(), entry.getValue().remoteAddr);
                    iterator.remove();
                }
            }
        } catch (ConcurrentModificationException cme) {
            // Swallowing this exception, because 
            // it is not really a big deal.
        }
    }
    
    protected PageHandle createHandle() {
        PageHandle handle;
        //do {
        handle = new PageHandle(UUID.randomUUID().toString());
        //} while (pages.containsKey(handle));
        return handle;
    }

    @Override
    public void execute(PageHandle handle,
                        ScopedWebApplicationExecution execution) {
        Holder holder;
        synchronized (this) {
            holder = pages.get(handle);
        }
        if (holder != null) {
            synchronized (holder) {
                execution.execute(holder.application);    
            }
        } else {
            execution.execute(null);
        }
    }

    @Override
    public void storeLarge(PageHandle handle, String key, Object obj) {
        if (handle == null) {
            throw new IllegalArgumentException("Handle cannot be null");
        } else if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be null!");
        }
        synchronized (this) {
            Holder holder = pages.get(handle);
            if (holder == null) {
                throw new NoPageScopeException(handle);
            }
            if (obj == null) {
                holder.largeObjects.remove(key);
            } else {
                holder.largeObjects.put(key, obj);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T loadLarge(PageHandle handle, String key, Class<T> type) {
        if (handle == null) {
            throw new IllegalArgumentException("Handle cannot be null");
        } else if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("Key cannot be null!");
        }
        synchronized (this) {
            Holder holder = pages.get(handle);
            if (holder == null) {
                throw new NoPageScopeException(handle);
            }
            return (T) holder.largeObjects.get(key);
        }
    }

    @Override
    public void executeSynchronized(PageHandle handle, Runnable runnable) {
        Holder holder = getHolder(handle, null);
        
        if (holder != null) {
            synchronized (holder) {
                runnable.run();
            }
        } else {
            throw new NoPageScopeException(handle);
        }
    }
}