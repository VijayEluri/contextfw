package net.contextfw.web.application;

import javax.servlet.http.HttpServletRequest;

public interface PageFlowFilter {

    boolean beforePageCreate(int scopeCount, HttpServletRequest request);
    
    boolean beforePageUpdate(int scopeCount, HttpServletRequest request);
    
    void onPageCreate(int scopeCount, String remoteAddr, String handle);
    
    void onPageUpdate(int scopeCount, String remoteAddr, String handle, int updateCount);
    
    void pageRemoved(int scopeCount, String remoteAddr, String handle);
    
    void pageExpired(int scopeCount, String remoteAddr, String handle);
    
    String getRemoteAddr(HttpServletRequest request);
}
