package net.contextfw.web.commons.async.internal.websocket;

import net.contextfw.web.application.PageHandle;

public interface WebSocketService {

    boolean asyncUpdate(final PageHandle handle, final String componentId);

}