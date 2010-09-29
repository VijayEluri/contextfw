package net.contextfw.web.application.internal.providers;

import net.contextfw.web.application.request.Request;
import net.contextfw.web.service.application.HttpContext;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class RequestProvider implements Provider<Request> {

    @Inject
    private HttpContext context;
    
    @Override
    public Request get() {
        return new Request(context.getRequest());
    }

}
