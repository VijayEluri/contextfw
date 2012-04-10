package net.contextfw.web.application.internal.service;

import net.contextfw.web.application.PageHandle;
import net.contextfw.web.application.WebApplication;
import net.contextfw.web.application.internal.page.PageScope;
import net.contextfw.web.application.internal.page.WebApplicationPage;
import net.contextfw.web.application.lifecycle.PageScopedExecutor;
import net.contextfw.web.application.scope.NoPageScopeException;
import net.contextfw.web.application.scope.ScopedWebApplicationExecution;
import net.contextfw.web.application.scope.WebApplicationStorage;
import net.contextfw.web.application.util.Tracker;

public class PageScopedExecutorImpl implements PageScopedExecutor {

    private final WebApplicationStorage storage;

    private final PageScope pageScope;

    public PageScopedExecutorImpl(WebApplicationStorage storage, PageScope pageScope) {
        this.storage = storage;
        this.pageScope = pageScope;
        Tracker.initialized(this);
    }

    @Override
    public void execute(final PageHandle handle, final Runnable runnable) {
        storage.execute(handle,
                new ScopedWebApplicationExecution() {
                    @Override
                    public void execute(WebApplication application) {
                        if (application != null) {
                            WebApplicationPage page = (WebApplicationPage) application;
                            pageScope.activatePage(page, null, null, null);
                            try {
                                runnable.run();
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                            } finally {
                                pageScope.deactivateCurrentPage();
                            }
                        } else {
                            throw new NoPageScopeException(handle);
                        }
                    }
                });
    }
}
