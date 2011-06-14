package net.contextfw.web.application;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.lifecycle.PageScoped;

/**
 * This class contains functionality to access basic servlet resources and make basic
 * controls.
 * 
 */
@PageScoped
public class HttpContext {

    private HttpServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;

    private final String requestURI;
    private final String queryString;
    private String redirectUrl;
    private String errorMsg;
    private Integer errorCode;
    private boolean reload = false;

    /**
     * Reloads current page
     * 
     * <p>
     *  This method has effect only during page update.  
     * </p>
     */
    public void reload() {
        reload = true;
    }
    
    /**
     * Redirects page to new location
     * 
     * @param url
     *  The new URL
     */
    public void redirect(String url) {
        this.redirectUrl = url;
    }
    
    /**
     * Sends a server error.
     * 
     * <p>
     *  Usable only on init phase
     * </p>
     * 
     * @param code
     *  Http response code
     */
    public void sendError(int code) {
        sendError(code, null);
    }
    
    /**
     * Sends a server error with a message.
     * 
     * <p>
     *  Usable only on init phase
     * </p>
     * 
     * @param code
     *  Http response code
     * @param msg
     *  Message
     */
    public void sendError(int code, String msg) {
        this.errorCode = code;
        this.errorMsg = msg;
    }

    public void setServlet(HttpServlet servlet) {
        this.servlet = servlet;
    }

    /**
     * Gets current servlet used for the request
     */
    public HttpServlet getServlet() {
        return servlet;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Gets current request
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
    
    /**
     * Gets current response for the request
     */
    public HttpServletResponse getResponse() {
        return response;
    }

    public HttpContext(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response) {
        super();
        this.servlet = servlet;
        this.request = request;
        this.response = response;

        requestURI = request.getRequestURI();
        queryString = request.getQueryString();
    }


    public String getRedirectUrl() {
        return redirectUrl;
    }
    public String getErrorMsg() {
        return errorMsg;
    }
    public Integer getErrorCode() {
        return errorCode;
    }

    public boolean isReload() {
        return reload;
    }

    /**
     * Returns the original request URI when page was initialized
     * 
     * @return
     *   The original request URI
     * 
     */
    public String getRequestURI() {
        return requestURI;
    }

    /**
     * Returns the original query string when page was initialized
     * 
     * @return
     *   The original query string
     * 
     */
    public String getQueryString() {
        return queryString;
    }
}