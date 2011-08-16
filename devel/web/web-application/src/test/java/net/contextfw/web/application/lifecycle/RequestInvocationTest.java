package net.contextfw.web.application.lifecycle;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.lifecycle.RequestInvocationFilter.Mode;

import org.junit.Test;

public class RequestInvocationTest {

    @Test
    public void Request_Is_Invoked() throws ServletException, IOException {
        RequestInvocation invocation = createStrictMock(RequestInvocation.class);
        HttpServletRequest request = createStrictMock(HttpServletRequest.class);
        HttpServletResponse response = createStrictMock(HttpServletResponse.class);
        invocation.invoke(request, response);
        replay(invocation);
        DefaultRequestInvocationFilter filter = new DefaultRequestInvocationFilter();
        filter.filter(Mode.INIT, request, response, invocation);
        verify(invocation);
    }
}
