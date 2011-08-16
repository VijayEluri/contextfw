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
