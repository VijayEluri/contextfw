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

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.PageContext;
import net.contextfw.web.application.lifecycle.LifecycleListener;
import net.contextfw.web.application.util.Tracker;

import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;

/*
 * PageStorage
 * 
 *   - store(WebApplicationHandle handle, 
 * 
 * WebPage
 * 
 */

public class PageScope implements Scope {
    
    //private Logger log = LoggerFactory.getLogger(PageScope.class);

    private LifecycleListener listener;
    
    private final ThreadLocal<WebApplicationPage> currentPage = 
        new ThreadLocal<WebApplicationPage>();

    public PageScope() {
        Tracker.initialized(this);
    }
    
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {
            public T get() {
                WebApplicationPage page = currentPage.get();
                if (page != null) {
                    T bean = (T) page.getBean(key);
                    if (bean != null) {
                        return bean;
                    } else { 
                       return page.setBean(key, unscoped.get());
                    }
                } else {
                    throw new OutOfScopeException("PageScope does not exist!");
                }
            }
        };
    }
    
    public void deactivateCurrentPage() {
        WebApplicationPage page = currentPage.get();
        if (page != null) {
            listener.beforePageScopeDeactivation();
            PageContext context = page.getBean(Key.get(PageContext.class));
            context.setServlet(null);
            context.setRequest(null);
            context.setResponse(null);
            currentPage.remove();
        }
    }

    public void activatePage(WebApplicationPage page,
                             HttpServlet servlet,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        
        PageContext context = page.getBean(Key.get(PageContext.class));
        context.setServlet(servlet);
        context.setRequest(request);
        context.setResponse(response);
        currentPage.set(page);
        listener.afterPageScopeActivation();
    }

    public synchronized WebApplicationPage createPage(HttpServlet servlet,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response) {
        
        WebApplicationPage page = new WebApplicationPageImpl();
        
        page.setBean(Key.get(PageContext.class), 
                new PageContext(servlet, request, response));
        
        currentPage.set(page);
        return page;
    }

    //@Inject
    public void setListener(LifecycleListener listener) {
        this.listener = listener;
    }
}
