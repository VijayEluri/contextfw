package net.contextfw.web.application;

import javax.servlet.http.HttpServletRequest;

public class DefaultPageFlowFilter implements PageFlowFilter {

    @Override
    public void pageRemoved(int scopeCount, String remoteAddr, String handle) {
    }

    @Override
    public void pageExpired(int scopeCount, String remoteAddr, String handle) {
    }

    @Override
    public String getRemoteAddr(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    @Override
    public void onPageCreate(int scopeCount, String remoteAddr, String handle) {
    }

    @Override
    public void onPageUpdate(int scopeCount, String remoteAddr, String handle, int updateCount) {
    }

    @Override
    public boolean beforePageCreate(int scopeCount, HttpServletRequest request) {
        return true;
    }

    @Override
    public boolean beforePageUpdate(int scopeCount, HttpServletRequest request) {
        return true;
    }
}
