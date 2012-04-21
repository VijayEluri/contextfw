package net.contextfw.web.commons.async.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.contextfw.web.application.PageHandle;

public class AsyncServerInfo {

    private Map<PageHandle, String> hosts = new HashMap<PageHandle, String>();
    
    private Map<PageHandle, Set<String>> refreshRequests = new HashMap<PageHandle, Set<String>>();
    
    private Set<String> currentUpdates = new HashSet<String>();
    
    public String setHost(PageHandle handle, String host) {
        return hosts.put(handle, host);
    }
    
    public String getHost(PageHandle handle) {
        return hosts.get(handle);
    }
    
    public void removeHost(PageHandle handle) {
        this.hosts.remove(handle);
    }
    
    public void addRefreshRequest(PageHandle handle, String componentId) {
        if (!refreshRequests.containsKey(handle)) {
            refreshRequests.put(handle, new HashSet<String>());
        }
        refreshRequests.get(handle).add(componentId);
    }
    
    public Set<String> purgeRefreshRequests(PageHandle handle) {
        return refreshRequests.remove(handle);
    }
    
    public void setUpdating(String id, boolean updating) {
        if (updating) {
            currentUpdates.add(id);
        } else {
            currentUpdates.remove(id);
        }
    }
    
    public boolean isUpdating(String id) {
        return currentUpdates.contains(id);
    }
}
