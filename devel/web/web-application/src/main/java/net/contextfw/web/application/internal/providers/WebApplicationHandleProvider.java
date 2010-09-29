package net.contextfw.web.application.internal.providers;

import net.contextfw.web.application.internal.scope.WebApplicationScopedBeans;
import net.contextfw.web.service.application.WebApplicationHandle;

import com.google.inject.Key;
import com.google.inject.Provider;

public class WebApplicationHandleProvider implements Provider<WebApplicationHandle> {

    @Override
    public WebApplicationHandle get() {
        return (WebApplicationHandle) WebApplicationScopedBeans.getCurrentInstance().getBeans().get(
                Key.get(WebApplicationHandle.class));
    }

}
