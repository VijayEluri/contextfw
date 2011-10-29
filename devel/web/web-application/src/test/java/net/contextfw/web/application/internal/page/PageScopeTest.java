/**
 * Copyright 2010 Marko Lavikainen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.contextfw.web.application.internal.page;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.application.AbstractTest;
import net.contextfw.web.application.HttpContext;
import net.contextfw.web.application.WebApplication;
import net.contextfw.web.application.WebApplicationHandle;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.lifecycle.LifecycleListener;
import net.contextfw.web.application.scope.DefaultWebApplicationStorage;
import net.contextfw.web.application.scope.ScopedWebApplicationExecution;

import org.apache.commons.lang.mutable.MutableBoolean;
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
    
    private DefaultWebApplicationStorage storage;
    
    private WebApplicationPage page;
    
    @Before
    public void setup() throws IOException {
        storage = new DefaultWebApplicationStorage(
                Configuration.getDefaults());
        
        pageScope = new PageScope();
        pageScope.setListener(createMock(LifecycleListener.class));
        request = createMock(HttpServletRequest.class);
        servlet = createMock(HttpServlet.class);
        response = createMock(HttpServletResponse.class);
        expect(request.getRequestURI()).andReturn("/test");
        expect(request.getQueryString()).andReturn(null);
        expect(request.getRemoteAddr()).andReturn(LOCALHOST);
        replay(request, servlet, response);
        servlet = createMock(HttpServlet.class);
        response = createMock(HttpServletResponse.class);
        
        page = pageScope.createPage(servlet, request, response);
        
        final MutableBoolean executionRun = new MutableBoolean(false);
        storage.initialize(page, 
                request, 
                System.currentTimeMillis() + MAX_INACTIVITY,
                Thread.currentThread().getContextClassLoader(),
                new ScopedWebApplicationExecution() {
            @Override
            public void execute(WebApplication application) {
                executionRun.setValue(true);
                assertEquals(page, application);
            }
        });
        assertTrue(executionRun.booleanValue());
    }
    
    @Test(expected=OutOfScopeException.class)
    public void No_Page_Scope() {
        pageScope.deactivateCurrentPage();
        pageScope.scope(Key.get(PageScopedBean.class), null).get();
    }
    
    @Test
    public void Get_Bean() {
        PageScopedBean scoped = new PageScopedBean();
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
        assertEquals(page.getHandle(), 
                pageScope.scope(Key.get(WebApplicationHandle.class), null).get());
    }
    
    @Test
    public void Http_Context_Is_Set_And_Cleared() {
        
        WebApplicationPage page = pageScope.createPage(servlet, request, response);
        HttpContext context = page.getBean(Key.get(HttpContext.class));
        
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
