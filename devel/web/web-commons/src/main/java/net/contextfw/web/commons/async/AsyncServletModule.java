package net.contextfw.web.commons.async;


import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.commons.async.AsyncConf.AsyncMode;
import net.contextfw.web.commons.async.internal.AsyncServiceImpl;
import net.contextfw.web.commons.async.internal.InternalAsyncService;
import net.contextfw.web.commons.async.internal.comet.AsyncRefreshServlet;
import net.contextfw.web.commons.async.internal.comet.JettyAsyncServlet;
import net.contextfw.web.commons.async.internal.websocket.DummyWebSocketService;
import net.contextfw.web.commons.async.internal.websocket.WebSocketService;
import net.contextfw.web.commons.async.internal.websocket.WebSocketServiceImpl;

import com.google.inject.servlet.ServletModule;

public class AsyncServletModule extends ServletModule {

    private final AsyncMode mode;
    private final boolean multiNodeSupport;
    private final boolean webSocketEnabled;
    
    public AsyncServletModule(Configuration conf) {
        mode = conf.getOrElse(AsyncConf.MODE, AsyncMode.NONE);
        multiNodeSupport = conf.getOrElse(AsyncConf.MULTI_NODE_SUPPORT, false);
        webSocketEnabled = conf.getOrElse(AsyncConf.WEB_SOCKET_ENABLED, false);
    }
    
    @Override
    protected void configureServlets() {
        
        if (webSocketEnabled) {
            bind(WebSocketService.class).to(WebSocketServiceImpl.class).asEagerSingleton();
        } else {
            bind(WebSocketService.class).to(DummyWebSocketService.class);
        }

        bind(AsyncService.class).to(AsyncServiceImpl.class);
        bind(InternalAsyncService.class).to(AsyncServiceImpl.class);
        
        if (multiNodeSupport) {
            serve("/asyncRefresh").with(AsyncRefreshServlet.class);
        }
        if (mode == AsyncMode.JETTY) {
            serve("/async/*").with(JettyAsyncServlet.class);
        }
    }
}
