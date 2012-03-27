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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.PageHandle;
import net.contextfw.web.application.ResourceCleaner;
import net.contextfw.web.application.WebApplication;
import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.internal.page.PageScope;
import net.contextfw.web.application.internal.page.WebApplicationPage;
import net.contextfw.web.application.lifecycle.LifecycleListener;
import net.contextfw.web.application.remote.ResourceResponse;
import net.contextfw.web.application.scope.ScopedWebApplicationExecution;
import net.contextfw.web.application.scope.WebApplicationStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class UpdateHandler {

    private static final String CONTEXTFW_REFRESH = "contextfw-refresh";

    private static final String CONTEXTFW_UPDATE = "contextfw-update";

    private static final String CONTEXTFW_REMOVE = "contextfw-remove";
    
    private Logger logger = LoggerFactory.getLogger(UpdateHandler.class);

    private final LifecycleListener listeners;

    private final Gson gson;

    private final DirectoryWatcher watcher;

    private final ResourceCleaner cleaner;

    private PageScope pageScope;

    private final long maxInactivity;

    private final WebApplicationStorage storage;

    @Inject
    public UpdateHandler(LifecycleListener listeners,
            DirectoryWatcher watcher,
            ResourceCleaner cleaner,
            WebApplicationStorage storage,
            Configuration configuration,
            PageScope pageScope,
            Gson gson) {

        this.gson = gson;
        this.listeners = listeners;
        this.maxInactivity = configuration.get(Configuration.MAX_INACTIVITY);
        this.storage = storage;
        this.pageScope = pageScope;

        if (configuration.get(Configuration.DEVELOPMENT_MODE)) {
            this.cleaner = cleaner;
            this.watcher = watcher;
        } else {
            this.cleaner = null;
            this.watcher = null;
        }
    }

    private int getCommandStart(String[] splits) {
        int remaining = splits.length;
        for (int i = 0; i < splits.length; i++) {
            remaining--;
            String s = splits[i];
            
            if ((CONTEXTFW_REMOVE.equals(s) || CONTEXTFW_REFRESH.equals(s)) && remaining >= 1) {
                return i;
            } else if (CONTEXTFW_UPDATE.equals(s) && remaining >= 3) {
                return i;
            }
        }
        return -1;
    }

    public final void handleRequest(final HttpServlet servlet,
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {

        if (watcher != null && watcher.hasChanged()) {
            logger.info("Reloading resources");
            cleaner.clean();
        }

        final String[] uriSplits = request.getRequestURI().split("/");
        final int commandStart = getCommandStart(uriSplits);
        if (commandStart != -1) {

            String command = uriSplits[commandStart];
            PageHandle handle = new PageHandle(uriSplits[commandStart + 1]);

            if (CONTEXTFW_REMOVE.equals(command)) {
                storage.remove(handle, request);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else if (CONTEXTFW_REFRESH.equals(command)) {
                storage.refresh(handle, request, System.currentTimeMillis() + maxInactivity);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else if (CONTEXTFW_UPDATE.equals(command)) {
                
                final UpdateInvocation[] invocation = new UpdateInvocation[1];
                invocation[0] = UpdateInvocation.NOT_DELAYED;
                storage.update(
                        handle, 
                        request,
                        System.currentTimeMillis() + maxInactivity, 
                        new ScopedWebApplicationExecution() {
                    @Override
                    public void execute(WebApplication application) {
                        if (application == null) {
                            try {
                                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                            } catch (IOException e) {
                                throw new WebApplicationException(e);
                            }
                        } else {
                            WebApplicationPage page = (WebApplicationPage) application;
                            pageScope.activatePage(page, servlet, request, response);
                            try {
                                invocation[0] =
                                        page.getWebApplication().updateState(
                                                uriSplits[commandStart + 2],
                                                uriSplits[commandStart + 3]);
                                if (invocation[0].isDelayed()) {
                                    pageScope.deactivateCurrentPage();
                                    return;
                                }

                                if (!invocation[0].isResource() && !invocation[0].isCancelled()) {
                                    listeners.beforeRender();
                                    setHeaders(response);
                                    response.setContentType("text/xml; charset=UTF-8");
                                    page.getWebApplication().sendResponse();
                                    listeners.afterRender();
                                }
                            } catch (Exception e) {
                                listeners.onException(e);
                            } finally {
                                pageScope.deactivateCurrentPage();
                            }
                        }
                    }
                });

                if (invocation[0].isResource()) {
                    handleResource(request, response, invocation[0]);
                }
            }
        }
        else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void handleResource(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    UpdateInvocation invocation) throws IOException {

        if (invocation.getRetVal() == null) {
            response.getWriter().close();
        }
        if (invocation.getRetVal() instanceof ResourceResponse) {
            ((ResourceResponse) invocation.getRetVal()).serve(request, response);
        } else {
            setHeaders(response);
            response.setContentType("application/json; charset=UTF-8");
            gson.toJson(invocation.getRetVal(), response.getWriter());
            response.getWriter().close();
        }
    }

    public void setHeaders(HttpServletResponse response) {
        response.addHeader("Expires", "Sun, 19 Nov 1978 05:00:00 GMT");
        response.addHeader("Last-Modified", new Date().toString());
        response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // response.addHeader("Cache-Control","post-check=0, pre-check=0");
        response.addHeader("Pragma", "no-cache");
        response.setHeader("Connection", "Keep-Alive");
    }

    @Inject
    public void setPageScope(PageScope pageScope) {
        this.pageScope = pageScope;
    }
}