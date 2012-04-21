package net.contextfw.web.commons.async.internal.websocket;

import net.contextfw.web.application.PageHandle;

public class DummyWebSocketService implements WebSocketService {

    @Override
    public boolean asyncUpdate(PageHandle handle, String componentId) {
        return false;
    }

}
