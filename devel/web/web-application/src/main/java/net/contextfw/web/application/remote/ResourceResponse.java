package net.contextfw.web.application.remote;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Defines the implementation for custom response to web client.
 * 
 * <p>
 *  It should be noted that resource is served outside of page context
 *  so all needed information must be given before hand to implementin class.
 *  The reason for this behavior is that creating the response may take time
 *  and user interaction would be blocked if resource is compiled in page scope. 
 * </p>
 * 
 * 
 * @see ResourceBody
 *
 */
public interface ResourceResponse {
    void serve(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
