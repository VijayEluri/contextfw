package net.contextfw.web.application.remote;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Defines the implementation for custom response to web client
 * 
 * @see ResourceBody
 *
 */
public interface ResourceResponse {
    public void serve(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
