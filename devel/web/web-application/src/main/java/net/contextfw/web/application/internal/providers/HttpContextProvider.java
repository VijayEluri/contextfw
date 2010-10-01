package net.contextfw.web.application.internal.providers;

import net.contextfw.web.application.HttpContext;
import net.contextfw.web.application.internal.scope.WebApplicationScopedBeans;

import com.google.inject.Key;
import com.google.inject.Provider;

public class HttpContextProvider implements Provider<HttpContext> {

    @Override
    public HttpContext get() {
        return (HttpContext) WebApplicationScopedBeans.getCurrentInstance().getBeans().get(Key.get(HttpContext.class));
    }

}
