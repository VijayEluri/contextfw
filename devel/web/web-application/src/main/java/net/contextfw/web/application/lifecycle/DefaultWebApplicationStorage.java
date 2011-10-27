package net.contextfw.web.application.lifecycle;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import net.contextfw.web.application.WebApplication;
import net.contextfw.web.application.WebApplicationHandle;
import net.contextfw.web.application.configuration.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultWebApplicationStorage implements WebApplicationStorage {

    private Logger logger = LoggerFactory.getLogger(DefaultWebApplicationStorage.class);

    private final Map<WebApplicationHandle, WebApplication> pages =
            Collections.synchronizedMap(new HashMap<WebApplicationHandle, WebApplication>());

    @Inject
    public DefaultWebApplicationStorage(Configuration configuration,
            final PageFlowFilter pageFlowFilter) {

        Timer timer = new Timer(true);
        logger.info("Starting scheduled removal for expired web applications");

        timer.schedule(new TimerTask() {
            public void run() {
                removeExpiredPages(pageFlowFilter);
            }
        }, configuration.get(Configuration.REMOVAL_SCHEDULE_PERIOD),
                configuration.get(Configuration.REMOVAL_SCHEDULE_PERIOD));

    }

    @Override
    public boolean remove(WebApplicationHandle handle) {
        return pages.remove(handle) != null;
    }

    public synchronized void removeExpiredPages(PageFlowFilter filter) {

        long timestamp = System.currentTimeMillis();

        Iterator<Entry<WebApplicationHandle, WebApplication>> iterator =
                pages.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<WebApplicationHandle, WebApplication> entry = iterator.next();
            if (entry.getValue().isExpired(timestamp)) {
                filter.pageExpired(pages.size(),
                        entry.getValue().getRemoteAddr(),
                        entry.getKey().getKey());
                iterator.remove();
            }
        }
    }

    @Override
    public WebApplicationHandle createHandle() {
        WebApplicationHandle handle;
        do {
            handle = new WebApplicationHandle(UUID.randomUUID().toString());
        } while (pages.containsKey(handle));

        return handle;
    }

    @Override
    public Integer refresh(WebApplicationHandle handle, 
                           String remoteAddr, 
                           long maxInactivity) {
        
        WebApplication application = pages.get(handle);

        if (application != null) {
            if (application.getRemoteAddr().equals(remoteAddr)) {
                return application.refresh(System.currentTimeMillis() + maxInactivity);
            } else {
                logger.info("Tried to refresh page {} from wrong address: {} != {}",
                        new String[] { handle.getKey(),
                        application.getRemoteAddr(), 
                        remoteAddr });
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void execute(WebApplicationHandle handle, 
                        String remoteAddr, 
                        ScopedExecution execution) throws IOException {
        
        WebApplication application = pages.get(handle);
        
        if (application != null) {
                if (application.getRemoteAddr().equals(remoteAddr)) {
                    synchronized(application) {
                        execution.execute(application);
                    }
                } else {
                    logger.info("Tried to activate page {} from wrong address: {} != {}",
                            new String[] { handle.getKey(),
                            application.getRemoteAddr(), 
                            remoteAddr });
                    execution.execute(null);
                }
        } else {
            execution.execute(null);
        }
    }

    @Override
    public void execute(WebApplicationHandle handle, 
                        WebApplication application, 
                        String remoteAddr,
                        ScopedExecution execution) throws IOException {
        
        pages.put(handle, application);
        
        synchronized(application) {
            execution.execute(application);
        }
    }

    @Override
    public int getPageCount() {
        return pages.size();
    }
}
