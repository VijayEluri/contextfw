package net.contextfw.web.application.internal.service;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.application.AbstractGuiceTest;
import net.contextfw.web.application.HttpContext;
import net.contextfw.web.application.WebApplicationHandle;
import net.contextfw.web.application.internal.scope.PageScopedBeans;

import org.junit.Before;
import org.junit.Test;

public class WebApplicationGuiceTest extends AbstractGuiceTest {
    
    private WebApplication webApplication;
    
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpServlet servlet;
    
    @Before
    public void setup() {
        WebApplicationContextHandler handler = getMember(WebApplicationContextHandler.class);
        request = createNiceMock(HttpServletRequest.class);
        expect(request.getRequestURI()).andReturn("/test");
        expect(request.getQueryString()).andReturn(null);
        replay(request);
        PageScopedBeans.createNewInstance();
        PageScopedBeans.getCurrentInstance().seed(HttpContext.class, 
                new HttpContext(servlet, request, response));
        PageScopedBeans.getCurrentInstance().seed(WebApplicationHandle.class, 
                handler.createNewHandle());
        webApplication = getMember(WebApplication.class);
        
    }
    
    @Test
    public void Test() {
        
    }

}
