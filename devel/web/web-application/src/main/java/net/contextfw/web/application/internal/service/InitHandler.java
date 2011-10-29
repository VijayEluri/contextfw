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

package net.contextfw.web.application.internal.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.ResourceCleaner;
import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.internal.component.MetaComponentException;
import net.contextfw.web.application.internal.page.PageScope;
import net.contextfw.web.application.internal.page.WebApplicationPage;
import net.contextfw.web.application.internal.servlet.UriMapping;
import net.contextfw.web.application.lifecycle.LifecycleListener;
import net.contextfw.web.application.remote.ErrorResolution;
import net.contextfw.web.application.scope.ScopedWebApplicationExecution;
import net.contextfw.web.application.scope.WebApplicationStorage;

import org.apache.commons.lang.mutable.MutableBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class InitHandler {

    private Logger logger = LoggerFactory.getLogger(InitHandler.class);

    private static final long HOUR = 1000 * 60 * 3600;

    // private final InitializerProvider initializers;
    @Inject
    private Provider<WebApplication> webApplicationProvider;
    @Inject
    private LifecycleListener listeners;
    
    private PageScope pageScope;
    
    @Inject
    private WebApplicationStorage storage;

    private final long initialMaxInactivity;

    private DirectoryWatcher watcher;

    private ResourceCleaner cleaner;

    private final boolean developmentMode;

    public InitHandler(Configuration properties, PageScope pageScope) {
        initialMaxInactivity = properties.get(Configuration.INITIAL_MAX_INACTIVITY);
        developmentMode = properties.get(Configuration.DEVELOPMENT_MODE);
        this.pageScope = pageScope;
    }

    public final void handleRequest(
            final UriMapping mapping,
            final List<Class<? extends Component>> chain,
            final HttpServlet servlet,
            final HttpServletRequest request,
            final HttpServletResponse response,
            ClassLoader classLoader)
            throws ServletException, IOException {

        if (watcher != null && watcher.hasChanged()) {
            logger.debug("Reloading resources");
            cleaner.clean();
        }

        if (!listeners.beforeInitialize(servlet, request, response)) {
            return;
        }

        response.addHeader("Expires", "Sun, 19 Nov 1978 05:00:00 GMT");
        response.addHeader("Last-Modified", new Date().toString());
        response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.addHeader("Pragma", "no-cache");

        if (chain == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {

            WebApplicationPage page = pageScope.createPage(servlet, request, response);
            final MutableBoolean expired = new MutableBoolean(false);
            storage.initialize(
                    page,
                    request,
                    System.currentTimeMillis() + HOUR,
                    classLoader,
                    new ScopedWebApplicationExecution() {
                        @Override
                        public void execute(net.contextfw.web.application.WebApplication application) {
                            try {
                                WebApplicationPage page = (WebApplicationPage) application;
                                WebApplication app = webApplicationProvider.get();
                                app.setInitializerChain(chain);
                                page.setWebApplication(app);
                                page.getWebApplication().initState(mapping);
                                listeners.afterInitialize();
                                listeners.beforeRender();
                                expired.setValue(page.getWebApplication().sendResponse());
                                listeners.afterRender();
                            } catch (Exception e) {
                                // TODO Fix this construct with test
                                if (e instanceof MetaComponentException) {
                                    ErrorResolution resolution =
                                            ((MetaComponentException) e).getResolution();
                                    try {
                                        if (resolution == ErrorResolution.SEND_NOT_FOUND_ERROR) {
                                            response.sendError(HttpServletResponse.SC_NOT_FOUND);
                                        } else if (resolution == ErrorResolution.SEND_BAD_REQUEST_ERROR) {
                                            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                                        }
                                    } catch (IOException e1) {
                                        throw new WebApplicationException(e1);
                                    }
                                }
                                listeners.onException(e);
                            } finally {
                                pageScope.deactivateCurrentPage();
                            }

                        }
                    });
            // Setting expiration here so that long page
            // processing is
            // not
            // penalizing client
            if (expired.booleanValue()) {
                storage.remove(page.getHandle(), request);
            } else {
                storage.refresh(
                        page.getHandle(), 
                        request,
                        System.currentTimeMillis() + initialMaxInactivity);
            }
        }
    }

    @Inject
    public void setWatcher(DirectoryWatcher watcher) {
        if (developmentMode) {
            this.watcher = watcher;
        }
    }

    @Inject
    public void setCleaner(ResourceCleaner cleaner) {
        if (developmentMode) {
            this.cleaner = cleaner;
        }
    }

    public void setPageScope(PageScope pageScope) {
        this.pageScope = pageScope;
    }
}