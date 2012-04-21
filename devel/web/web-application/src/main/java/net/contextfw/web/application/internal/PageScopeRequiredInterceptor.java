package net.contextfw.web.application.internal;

import net.contextfw.web.application.PageHandle;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class PageScopeRequiredInterceptor implements MethodInterceptor {

    private final Provider<PageHandle> pageHandle;
    
    @Inject
    public PageScopeRequiredInterceptor(Provider<PageHandle> pageHandle) {
        this.pageHandle = pageHandle;
    }
    
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        pageHandle.get();
        return invocation.proceed();
    }

}
