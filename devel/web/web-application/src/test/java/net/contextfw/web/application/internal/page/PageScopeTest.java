package net.contextfw.web.application.internal.page;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.application.AbstractTest;
import net.contextfw.web.application.HttpContext;
import net.contextfw.web.application.WebApplicationHandle;
import net.contextfw.web.application.lifecycle.PageFlowFilter;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;

public class PageScopeTest extends AbstractTest {

    private static final String LOCALHOST = "127.0.0.1";
    private static final long MAX_INACTIVITY = 700;
    
    private static class PageScopedBean {}
    
    private HttpServletRequest request;
    private HttpServlet servlet;
    private HttpServletResponse response;
    
    private PageScope pageScope;
    
    @Before
    public void setup() {
        pageScope = new PageScope();
        request = createMock(HttpServletRequest.class);
        servlet = createMock(HttpServlet.class);
        response = createMock(HttpServletResponse.class);
        expect(request.getRequestURI()).andReturn("/test");
        expect(request.getQueryString()).andReturn(null);
        replay(request, servlet, response);
        servlet = createMock(HttpServlet.class);
        response = createMock(HttpServletResponse.class);
    }
    
    @Test(expected=OutOfScopeException.class)
    public void No_Page_Scope() {
        pageScope.scope(Key.get(PageScopedBean.class), null).get();
    }
    
    @Test
    public void Get_Bean() {
        PageScopedBean scoped = new PageScopedBean();
        WebApplicationPage page = pageScope.createPage(LOCALHOST, servlet, request, response, MAX_INACTIVITY);
        assertNotNull(page.getHandle());
        @SuppressWarnings("unchecked")
        Provider<PageScopedBean> unscoped = createMock(Provider.class);
        expect(unscoped.get()).andReturn(scoped);
        replay(unscoped);
        assertEquals(scoped, pageScope.scope(Key.get(PageScopedBean.class), unscoped).get());
        verify(unscoped);
        assertEquals(scoped, pageScope.scope(Key.get(PageScopedBean.class), null).get());
    }
    
    @Test
    public void Get_Initially_PageScoped_Beans() {
        WebApplicationPage page = pageScope.createPage(LOCALHOST, servlet, request, response, MAX_INACTIVITY);
        assertEquals(page.getHandle(), 
                pageScope.scope(Key.get(WebApplicationHandle.class), null).get());
    }
    
    @Test
    public void Refresh_Increments_Count() {
        WebApplicationPage page = pageScope.createPage(LOCALHOST, servlet, request, response, MAX_INACTIVITY);
        assertEquals(1, pageScope.refreshPage(page.getHandle(), LOCALHOST, MAX_INACTIVITY));
        assertEquals(2, pageScope.refreshPage(page.getHandle(), LOCALHOST, MAX_INACTIVITY));
    }
    
    @Test
    public void Page_Is_Not_Expired() {
        long now = System.currentTimeMillis();
        WebApplicationPage page = pageScope.createPage(LOCALHOST, servlet, request, response, MAX_INACTIVITY);
        pageScope.refreshPage(page.getHandle(), LOCALHOST, MAX_INACTIVITY);
        assertFalse(page.isExpired(now));
    }
    
    @Test
    public void Page_Is_Expired() {
        long now = System.currentTimeMillis();
        WebApplicationPage page = pageScope.createPage(LOCALHOST, servlet, request, response, MAX_INACTIVITY);
        pageScope.refreshPage(page.getHandle(), LOCALHOST, MAX_INACTIVITY);
        assertTrue(page.isExpired(now + MAX_INACTIVITY + 1000));
    }
    
    @Test
    public void Expired_Page_Is_Removed() throws InterruptedException {
        PageFlowFilter filter = createMock(PageFlowFilter.class);
        replay(filter);
        WebApplicationPage page = pageScope.createPage(LOCALHOST, servlet, request, response, MAX_INACTIVITY);
        assertEquals(page, pageScope.findPage(page.getHandle(), LOCALHOST));
        pageScope.refreshPage(page.getHandle(), LOCALHOST, MAX_INACTIVITY);
        Thread.sleep(MAX_INACTIVITY + 1);
        pageScope.removeExpiredPages(filter);
        assertNull(pageScope.findPage(page.getHandle(), LOCALHOST));
    }
    
    @Test
    public void Activate_From_Wrong_Address() {
        WebApplicationPage page = pageScope.createPage(LOCALHOST, servlet, request, response, MAX_INACTIVITY);
        assertNull(pageScope.findPage(page.getHandle(), "10.0.0.1"));
    }
    
    @Test
    public void Refresh_From_Wrong_Address() {
        WebApplicationPage page = pageScope.createPage(LOCALHOST, servlet, request, response, MAX_INACTIVITY);
        assertEquals(0, pageScope.refreshPage(page.getHandle(), "10.0.0.1", MAX_INACTIVITY));
    }
    
    @Test
    public void Http_Context_Is_Set_And_Cleared() {
        
        WebApplicationPage page = pageScope.createPage(LOCALHOST, servlet, request, response, MAX_INACTIVITY);
        HttpContext context = page.getBean(Key.get(HttpContext.class));
        WebApplicationHandle handle = page.getBean(Key.get(WebApplicationHandle.class));
        
        assertNotNull(context.getServlet());
        assertNotNull(context.getRequest());
        assertNotNull(context.getResponse());
        
        pageScope.deactivateCurrentPage();
        
        assertNull(context.getServlet());
        assertNull(context.getRequest());
        assertNull(context.getResponse());
        
        pageScope.activatePage(page, servlet, request, response);
        
        assertNotNull(context.getServlet());
        assertNotNull(context.getRequest());
        assertNotNull(context.getResponse());
    }
}
