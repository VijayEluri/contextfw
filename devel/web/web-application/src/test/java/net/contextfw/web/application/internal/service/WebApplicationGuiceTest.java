package net.contextfw.web.application.internal.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.application.AbstractGuiceTest;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.lifecycle.PageScoped;
import net.contextfw.web.application.lifecycle.View;

import org.junit.Before;
import org.junit.Test;

public class WebApplicationGuiceTest extends AbstractGuiceTest {
    
    @PageScoped
    @View(url="test")
    private static class Page extends Component {
        
    }
    
    private WebApplication webApplication;
    
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpServlet servlet;
    
    private Configuration configuration = Configuration.getDefaults();
    
    @Before
    public void setup() {
//        WebApplicationContextHandler handler = getMember(WebApplicationContextHandler.class);
//        request = createNiceMock(HttpServletRequest.class);
//        response = createMock(HttpServletResponse.class);
//        expect(request.getRequestURI()).andReturn("/test");
//        expect(request.getContextPath()).andReturn("");
//        expect(request.getQueryString()).andReturn(null);
//        replay(request);
//        PageScopedBeans.createNewInstance();
//        PageScopedBeans.getCurrentInstance().seed(HttpContext.class, 
//                new HttpContext(servlet, request, response));
//        PageScopedBeans.getCurrentInstance().seed(WebApplicationHandle.class, 
//                handler.createNewHandle());
//        webApplication = getMember(WebApplication.class);
        
    }
    
    @Test
    public void test() throws ServletException, IOException {
//        InitializerProvider provider = new InitializerProvider();
//        InitHandler initHandler = injectMembers(new InitHandler(configuration));
//        UriMapping mapping = createMock(UriMapping.class);
//        replay(mapping);
//        
//        initHandler.handleRequest(
//                mapping, 
//                provider.getInitializerChain(Page.class), 
//                servlet, 
//                request, 
//                response);
    }

}
