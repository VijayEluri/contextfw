package net.contextfw.web.commons.async.internal.websocket;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import net.contextfw.web.application.PageHandle;
import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.component.ComponentRegister;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.lifecycle.UpdateExecutor;
import net.contextfw.web.commons.async.AsyncConf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.WebSocketConnection;
import org.webbitserver.WebSocketHandler;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class WebSocketServiceImpl implements WebSocketHandler, WebSocketService {

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketServiceImpl.class);

    private final Map<String, WebSocketConnection> connections = 
            new HashMap<String, WebSocketConnection>();
    
    private final UpdateExecutor executor;
    
    private final WebServer webServer;

    private final Provider<ComponentRegister> register;
    
    @Inject
    public WebSocketServiceImpl(Configuration conf,
                                UpdateExecutor executor, 
                                Provider<ComponentRegister> register) {
        this.executor = executor;
        
        boolean enabled = conf.getOrElse(AsyncConf.WEB_SOCKET_ENABLED, false);
        int port = conf.getOrElse(AsyncConf.WEBBIT_PORT, 8083);
        String path = conf.getOrElse(AsyncConf.WEBBIT_HANDLER_PATH, "/websocket");
        this.register = register;
        
        webServer = enabled ? WebServers.createWebServer(port).add(path, this) : null;
        if (webServer != null) {
            webServer.start();
        }
    }

    @Override
    public synchronized void onOpen(WebSocketConnection connection) {
        String handle = connection.httpRequest().queryParam("handle");
        LOG.debug("Opening connection: {}", handle);
        connections.put(handle, connection);
    }

    @Override
    public synchronized void onClose(WebSocketConnection connection) {
        String handle = connection.httpRequest().queryParam("handle");
        LOG.debug("Closing connection: {}", handle);
        connections.remove(handle);
    }

    @Override
    public void onMessage(WebSocketConnection connection, byte[] msg) {
        throw new UnsupportedOperationException();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void onMessage(WebSocketConnection connection, String msg) {
        List<String> splits = Arrays.asList(msg.split("&"));
        for (int i = 0; i < splits.size(); i++) {
            splits.set(i, decode(splits.get(i)));
        }
        
        PageHandle handle = new PageHandle(splits.get(0));
        String componentId = splits.get(1);
        String method = splits.get(2);
        List<String> params = (List<String>) (splits.size() > 3 ? 
                splits.subList(3, splits.size()) : Collections.emptyList());
        
        StringWriter out = new StringWriter();
        
        executor.update(handle, componentId, method, params, null, new PrintWriter(out));
        connection.send(out.toString());
    }
    
    private String decode(String encoded) {
        try {
            return URLDecoder.decode(encoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new WebApplicationException(e);
        }
    }
    
    @Override
    public synchronized boolean asyncUpdate(final PageHandle handle, final String componentId) {
        try {
            if (webServer != null) {
                WebSocketConnection conn = connections.get(handle.toString());
                if (conn != null) {
                    Callable<Boolean> call = new Callable<Boolean>() { public Boolean call() {
                        Component component = register.get().findComponent(Component.class, componentId);
                        if (component != null) {
                            component.refresh();
                            return true;
                        } else {
                            return false;
                        }
                    }};
                    
                    StringWriter out = new StringWriter();
                    if (executor.update(handle, call, null, new PrintWriter(out))) {
                        conn.send(out.toString());    
                    }
                }
                return conn != null;
            }
        } catch (Exception e) {
            LOG.debug("Excecption", e);
        }
        return false;
    }

    @Override
    public void onPing(WebSocketConnection connection, byte[] msg) {
        connection.pong(msg);
    }

    @Override
    public void onPong(WebSocketConnection connection, byte[] msg) {
    }
}
