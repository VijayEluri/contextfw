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

package net.contextfw.web.application.lifecycle;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.lifecycle.RequestInvocationFilter.Mode;

import org.junit.Test;

public class RequestInvocationTest {

    @Test
    public void Request_Is_Invoked() throws ServletException, IOException {
        RequestInvocation invocation = createStrictMock(RequestInvocation.class);
        HttpServlet servlet = createStrictMock(HttpServlet.class);
        HttpServletRequest request = createStrictMock(HttpServletRequest.class);
        HttpServletResponse response = createStrictMock(HttpServletResponse.class);
        invocation.invoke(servlet, request, response);
        replay(invocation);
        DefaultRequestInvocationFilter filter = new DefaultRequestInvocationFilter();
        filter.filter(Mode.INIT, servlet, request, response, invocation);
        verify(invocation);
    }
}
