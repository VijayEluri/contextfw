package net.contextfw.web.application.internal.page;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.contextfw.web.application.WebApplicationHandle;
import net.contextfw.web.application.lifecycle.PageFlowFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;

public class PageScope implements Scope {
    
    private Logger log = LoggerFactory.getLogger(PageScope.class);

    private final Map<WebApplicationHandle, WebApplicationPage> pages = 
        new HashMap<WebApplicationHandle, WebApplicationPage>();

    private final ThreadLocal<WebApplicationPage> currentPage = 
        new ThreadLocal<WebApplicationPage>();

    public PageScope() {
    }
    
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {
            public T get() {
                WebApplicationPage page = currentPage.get();
                if (page != null) {
                    T bean = (T) page.getBean(key);
                    if (bean != null) {
                        return bean;
                    } else { 
                       return page.setBean(key, unscoped.get());
                    }
                } else {
                    throw new OutOfScopeException("PageScope does not exist!");
                }
            }
        };
    }
    
    public void deactivateCurrentPage() {
        currentPage.remove();
    }

    public int refreshPage(WebApplicationPage page, long maxInactivity) {
        return page.refresh(System.currentTimeMillis() + maxInactivity);
    }
    
    public synchronized int refreshPage(WebApplicationHandle handle, 
                                        String remoteAddr,
                                        long maxInactivity) {
        WebApplicationPage page = pages.get(handle);
        if (page != null) {
            if (page.getRemoteAddr().equals(remoteAddr)) {
                return refreshPage(page, maxInactivity);
            } else {
                log.info("Tried to refresh page {} from wrong address: {} != {}",
                        new String[] { handle.getKey(),
                        page.getRemoteAddr(), 
                        remoteAddr });
                return 0;
            }
        } else {
            return 0;
        }
    }

    public int getPageCount() {
        return pages.size();
    }
    
    public synchronized WebApplicationPage activatePage(WebApplicationHandle handle, String remoteAddr) {
        WebApplicationPage page = pages.get(handle);
        if (page != null) {
            if (page.getRemoteAddr().equals(remoteAddr)) {
                currentPage.set(page);
                return page;
            } else {
                log.info("Tried to activate page {} from wrong address: {} != {}",
                        new String[] { handle.getKey(),
                        page.getRemoteAddr(), 
                        remoteAddr });
                return null;
            }
        } else {
            return null;
        }
    }

//    public synchronized WebApplicationContext getContext(String handleKey) {
//        return contexts.get(new WebApplicationHandle(handleKey));
//    }
//
//    public synchronized void addContext(WebApplicationContext context) {
//        contexts.put(context.getHandle(), context);
//    }

    public synchronized void removeExpiredPages(PageFlowFilter filter) {
        
        long timestamp = System.currentTimeMillis();
        
        Iterator<Entry<WebApplicationHandle, WebApplicationPage>> iterator = 
            pages.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<WebApplicationHandle, WebApplicationPage> entry = iterator.next();
            if (entry.getValue().isExpired(timestamp)) {
                filter.pageExpired(getPageCount(),
                        entry.getValue().getRemoteAddr(), 
                        entry.getKey().getKey());
                iterator.remove();
            }
        }
    }

    public synchronized WebApplicationPage createPage(String remoteAddr, long initialMaxInActivity) {
        WebApplicationPage page = new WebApplicationPageImpl(
                createNewHandle(), 
                remoteAddr,
                System.currentTimeMillis() + initialMaxInActivity);
        pages.put(page.getHandle(), page);
        currentPage.set(page);
        return page;
    }
    
    private WebApplicationHandle createNewHandle() {
        WebApplicationHandle handle;
        do {
            handle = new WebApplicationHandle(UUID.randomUUID().toString());
        } while (pages.containsKey(handle));
        
        return handle;
    }

    public synchronized void removePage(WebApplicationHandle handle) {
        pages.remove(handle);
    }
}