package net.contextfw.web.commons.cloud.storage;

import net.contextfw.web.application.WebApplication;
import net.contextfw.web.application.PageHandle;
import net.contextfw.web.application.scope.Provided;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class WebApplicationMock implements WebApplication {

    @Inject
    private PageScoped1 scoped1;
    
    @Inject
    private Provider<PageScoped2> scoped2;

    public PageScoped1 getScoped1() {
        return scoped1;
    }

    public void setScoped1(PageScoped1 scoped1) {
        this.scoped1 = scoped1;
    }

    public PageScoped2 getScoped2() {
        return scoped2.get();
    }

    public SingletonScoped getSingletonScoped() {
        return singletonScoped;
    }

    public void setSingletonScoped(SingletonScoped singletonScoped) {
        this.singletonScoped = singletonScoped;
    }
    
    public long getL() {
        return l;
    }

    public void setL(long l) {
        this.l = l;
    }

    private long l;

    @Provided
    @Inject
    private SingletonScoped singletonScoped;

    private PageHandle handle;

    @Override
    public void setHandle(PageHandle handle) {
        this.handle = handle;
    }
    
    public PageHandle getHandle() {
        return handle;
    }

}
