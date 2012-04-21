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
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.internal.Constants;
import net.contextfw.web.application.internal.service.InitHandler;
import net.contextfw.web.application.lifecycle.RequestInvocation;
import net.contextfw.web.application.lifecycle.RequestInvocationFilter;
import net.contextfw.web.application.lifecycle.RequestInvocationFilter.Mode;

public class InitServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
            value=Constants.SE_BAD_FIELD,
            justification=Constants.DEFAULT_JUSTIFICATION)
    private final List<Class<? extends Component>> chain;
    
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
            value=Constants.SE_BAD_FIELD, 
            justification=Constants.DEFAULT_JUSTIFICATION)
    private final InitHandler handler;
    
    private final transient RequestInvocationFilter filter;
    
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
            value={Constants.SE_BAD_FIELD}, 
            justification=Constants.DEFAULT_JUSTIFICATION)
    private final RequestInvocation invocation = new RequestInvocation() {
        @Override
        public void invoke(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            handler.handleRequest(getMapping(), chain, servlet, request, response);
        }
    };

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
            value={Constants.SE_BAD_FIELD, Constants.MSF_MUTABLE_SERVLET_FIELD}, 
            justification=Constants.DEFAULT_JUSTIFICATION)
    private UriMapping mapping;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        filter.filter(Mode.INIT, this, req, resp, invocation);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        filter.filter(Mode.INIT, this, req, resp, invocation);
    }

    public InitServlet(InitHandler handler,
                       List<Class<? extends Component>> chain,
                       RequestInvocationFilter filter) {
        this.handler = handler;
        this.chain = chain;
        this.filter = filter;
    }

    public void setMapping(UriMapping mapping) {
        this.mapping = mapping;
    }

    public UriMapping getMapping() {
        return mapping;
    }
}
