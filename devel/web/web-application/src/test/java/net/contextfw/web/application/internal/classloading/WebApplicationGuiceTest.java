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

package net.contextfw.web.application.internal.classloading;

import java.io.IOException;

import javax.servlet.ServletException;

import net.contextfw.application.AbstractGuiceTest;

import org.junit.Before;
import org.junit.Test;

public class WebApplicationGuiceTest extends AbstractGuiceTest {
    
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
