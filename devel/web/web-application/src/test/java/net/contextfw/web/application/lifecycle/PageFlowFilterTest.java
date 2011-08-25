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

import static org.junit.Assert.*;

import javax.servlet.http.HttpServletRequest;

import net.contextfw.web.application.lifecycle.DefaultPageFlowFilter;
import net.contextfw.web.application.lifecycle.PageFlowFilter;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import org.junit.Test;

public class PageFlowFilterTest {

    private PageFlowFilter filter = new DefaultPageFlowFilter();
    
    @Test
    public void Basic_Test() {
        assertTrue(filter.beforePageCreate(0, null, null));
        assertTrue(filter.beforePageUpdate(0, null, null));
    }
    
    @Test
    public void Remote_Address() {
        String host = "127.0.0.1";
        HttpServletRequest request = createNiceMock(HttpServletRequest.class);
        expect(request.getRemoteAddr()).andReturn(host);
        replay(request);
        assertEquals(host, filter.getRemoteAddr(request));
    }
}
