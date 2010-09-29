package net.contextfw.web.service.application;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.annotations.WebApplicationScoped;

@WebApplicationScoped
public class HttpContext {

    private HttpServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;

    private String requestUrl;
    private String queryString;

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

    public String getCurrentUrl() {

        if (queryString != null) {
            return requestUrl + "?" + queryString;
        }

        return requestUrl;
    }
}