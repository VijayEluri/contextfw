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

package net.contextfw.web.application.internal.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.internal.service.UpdateHandler;
import net.contextfw.web.application.lifecycle.RequestInvocation;
import net.contextfw.web.application.lifecycle.RequestInvocationFilter;
import net.contextfw.web.application.lifecycle.RequestInvocationFilter.Mode;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class UpdateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
            value="SE_BAD_FIELD", justification="I know what I'm doing")
    @Inject
    private transient UpdateHandler handler;
    
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
            value="SE_BAD_FIELD", justification="I know what I'm doing")
    @Inject
    private RequestInvocationFilter filter;
    
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
            value="SE_BAD_FIELD", justification="I know what I'm doing")
    private final RequestInvocation invocation = new RequestInvocation() {
        @Override
        public void invoke(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            handler.handleRequest(UpdateServlet.this, request, response, classLoader);
        }
    };

    private ClassLoader classLoader;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        filter.filter(Mode.UPDATE, req, resp, invocation);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        filter.filter(Mode.UPDATE, req, resp, invocation);
    }

    @Inject
    public UpdateServlet() {
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
