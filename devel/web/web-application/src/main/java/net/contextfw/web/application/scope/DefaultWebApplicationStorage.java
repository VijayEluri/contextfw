package net.contextfw.web.application.scope;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import net.contextfw.web.application.WebApplication;
import net.contextfw.web.application.WebApplicationHandle;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.configuration.SettableProperty;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultWebApplicationStorage implements WebApplicationStorage {

    private static final int MAX_LENGTH = 16;

    private Logger logger = LoggerFactory.getLogger(DefaultWebApplicationStorage.class);

    private final Map<WebApplicationHandle, Holder> pages = 
            new HashMap<WebApplicationHandle, Holder>();
    
    public static final SettableProperty<Boolean> PROXIED = 
            Configuration.createProperty(Boolean.class, 
                    DefaultWebApplicationStorage.class.getName() + ".proxied");
    
    private final boolean proxied;
    
    private static final class Holder {
        private long validThrough;
        private final String remoteAddr;
        private final WebApplication application;
        
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
        
        this.proxied = configuration.getOrElse(PROXIED, false);
        
        Timer timer = new Timer(true);
        logger.info("Starting scheduled removal for expired web applications");

        timer.schedule(new TimerTask() {
            public void run() {
                removeExpiredPages();
            }
        }, configuration.get(Configuration.REMOVAL_SCHEDULE_PERIOD),
                configuration.get(Configuration.REMOVAL_SCHEDULE_PERIOD));
    }
    
    @Override
    public void initialize(WebApplication application, 
                           HttpServletRequest request,
                           long validThrough, 
                           ScopedWebApplicationExecution execution) {
        
        WebApplicationHandle handle = createHandle();
        application.setHandle(handle);
        Holder holder = new Holder(application, getRemoteAddr(request), validThrough);
        synchronized(this) {
            pages.put(handle, holder);
        }
        synchronized (holder) {
            execution.execute(holder.application);
        }
    }

    @Override
    public void update(WebApplicationHandle handle, 
                       HttpServletRequest request,
                       long validThrough,
                       ScopedWebApplicationExecution execution) {
        
        Holder holder = getHolder(handle, request);
        
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
    public void refresh(WebApplicationHandle handle, 
                        HttpServletRequest request, 
                        long validThrough) {
        
        Holder holder = getHolder(handle, request);
        if (holder != null) {
            holder.validThrough = validThrough;
        }
    }
    
    private Holder getHolder(WebApplicationHandle handle, HttpServletRequest request) {
        Holder holder;
        synchronized (this) {
            holder = pages.get(handle);    
        }
        String remoteAddr = getRemoteAddr(request);
        long now = System.currentTimeMillis();
        if (holder != null && holder.remoteAddr.equals(remoteAddr) 
                && holder.validThrough >= now) {
            return holder;
        } else {
            return null;
        }
    }

    @Override
    public synchronized void remove(WebApplicationHandle handle,
                       HttpServletRequest request) {
        
        Holder holder = getHolder(handle, request);
        if (holder != null) {
            pages.remove(handle);
            pageRemoved(handle, pages.size(), getRemoteAddr(request));
        }
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
    
    protected void pageRemoved(WebApplicationHandle handle, int pageCount, String remoteAddr) {
        
    }
    
    protected void pageExpired(WebApplicationHandle handle, int pageCount, String remoteAddr) {
        
    }
    
    protected void throttle() {
        
    }
    
    private synchronized void removeExpiredPages() {

        long now = System.currentTimeMillis();

        try {
            Iterator<Entry<WebApplicationHandle, Holder>> iterator =
                pages.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<WebApplicationHandle, Holder> entry = iterator.next();
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
    
    protected WebApplicationHandle createHandle() {
        WebApplicationHandle handle;
        //do {
        handle = new WebApplicationHandle(UUID.randomUUID().toString());
        //} while (pages.containsKey(handle));
        return handle;
    }

    @Override
    public void execute(WebApplicationHandle handle,
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
}