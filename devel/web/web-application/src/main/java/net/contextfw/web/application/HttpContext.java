package net.contextfw.web.application;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.lifecycle.PageScoped;

/**
 * This class contains functionality to...
 * 
 */
@PageScoped
public class HttpContext {

    private HttpServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;

    private final String requestUrl;
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
    public void sendError(int code) {
        sendError(code, null);
    }
    public void sendError(int code, String msg) {
        this.errorCode = code;
        this.errorMsg = msg;
    }

    public void setServlet(HttpServlet servlet) {
        this.servlet = servlet;
    }

    public HttpServlet getServlet() {
        return servlet;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public HttpContext(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response) {
        super();
        this.servlet = servlet;
        this.request = request;
        this.response = response;

        requestUrl = request.getRequestURL().toString();
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

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getQueryString() {
        return queryString;
    }
    
    public String getFullUrl() {
        if (queryString == null) {
            return requestUrl;
        } else {
            return requestUrl + "?" + queryString;
        }
    }
}