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
import static org.junit.Assert.assertFalse;
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
import net.contextfw.web.application.lifecycle.DefaultPageFlowFilter;
import net.contextfw.web.application.lifecycle.DefaultWebApplicationStorage;
import net.contextfw.web.application.lifecycle.PageFlowFilter;
import net.contextfw.web.application.lifecycle.ScopedExecution;

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
                Configuration.getDefaults(),
                new DefaultPageFlowFilter());
        
        pageScope = new PageScope();
        
        pageScope.setStorage(storage);
        request = createMock(HttpServletRequest.class);
        servlet = createMock(HttpServlet.class);
        response = createMock(HttpServletResponse.class);
        expect(request.getRequestURI()).andReturn("/test");
        expect(request.getQueryString()).andReturn(null);
        replay(request, servlet, response);
        servlet = createMock(HttpServlet.class);
        response = createMock(HttpServletResponse.class);
        
        page = pageScope.createPage(LOCALHOST, servlet, request, response, MAX_INACTIVITY);
        
        final MutableBoolean executionRun = new MutableBoolean(false);
        storage.execute(page.getHandle(), page, LOCALHOST, new ScopedExecution() {
            @Override
            public void execute(WebApplication application) throws IOException {
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
    public void Refresh_Increments_Count() {
        assertEquals(1, storage.refresh(page.getHandle(), LOCALHOST, MAX_INACTIVITY).intValue());
        assertEquals(2, storage.refresh(page.getHandle(), LOCALHOST, MAX_INACTIVITY).intValue());
    }
    
    @Test
    public void Page_Is_Not_Expired() {
        long now = System.currentTimeMillis();
        storage.refresh(page.getHandle(), LOCALHOST, MAX_INACTIVITY);
        assertFalse(page.isExpired(now));
    }
    
    @Test
    public void Page_Is_Expired() {
        long now = System.currentTimeMillis();
        storage.refresh(page.getHandle(), LOCALHOST, MAX_INACTIVITY);
        assertTrue(page.isExpired(now + MAX_INACTIVITY + 1000));
    }
    
    @Test
    public void Expired_Page_Is_Removed() throws InterruptedException, IOException {
        PageFlowFilter filter = createMock(PageFlowFilter.class);
        replay(filter);
        {
            final MutableBoolean executionRun = new MutableBoolean(false);
            storage.execute(page.getHandle(), page, LOCALHOST, new ScopedExecution() {
                @Override
                public void execute(WebApplication application) throws IOException {
                    executionRun.setValue(true);
                    assertEquals(page, application);
                }
            });
            assertTrue(executionRun.booleanValue());
        }
        storage.refresh(page.getHandle(), LOCALHOST, MAX_INACTIVITY);
        Thread.sleep(MAX_INACTIVITY + 1);
        storage.removeExpiredPages(filter);
        {
            final MutableBoolean executionRun = new MutableBoolean(false);
            storage.execute(page.getHandle(), LOCALHOST, new ScopedExecution() {
                @Override
                public void execute(WebApplication application) throws IOException {
                    executionRun.setValue(true);
                    assertNull(application);
                }
            });
            assertTrue(executionRun.booleanValue());
        }
    }
    
    @Test
    public void Activate_From_Wrong_Address() throws IOException {
        {
            final MutableBoolean executionRun = new MutableBoolean(false);
            storage.execute(page.getHandle(), LOCALHOST, new ScopedExecution() {
                @Override
                public void execute(WebApplication application) throws IOException {
                    executionRun.setValue(true);
                }
            });
            assertTrue(executionRun.booleanValue());
        }
        {
            final MutableBoolean executionRun = new MutableBoolean(false);
            storage.execute(page.getHandle(), "10.0.0.1", new ScopedExecution() {
                @Override
                public void execute(WebApplication application) throws IOException {
                    executionRun.setValue(true);
                    assertNull(application);
                }
            });
            assertTrue(executionRun.booleanValue());
        }
    }
    
    @Test
    public void Refresh_From_Wrong_Address() throws IOException {
        {
            final MutableBoolean executionRun = new MutableBoolean(false);
            storage.execute(page.getHandle(), LOCALHOST, new ScopedExecution() {
                @Override
                public void execute(WebApplication application) throws IOException {
                    executionRun.setValue(true);
                }
            });
            assertTrue(executionRun.booleanValue());
        }
        assertNull(storage.refresh(page.getHandle(), "10.0.0.1", MAX_INACTIVITY));
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
