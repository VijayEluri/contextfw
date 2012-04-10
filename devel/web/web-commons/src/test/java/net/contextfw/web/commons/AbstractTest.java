package net.contextfw.web.commons;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;

import com.mongodb.MongoException;

public abstract class AbstractTest {

    protected static final String LOCALHOST = "127.0.0.1";
    protected static final String FOOBAR = "foobar";
    
    protected <T> T createMock(Class<T> cl) {
        return createNiceMock(cl);
    }
    
    protected <T> T createStrictMock(Class<T> cl) {
        return EasyMock.createStrictMock(cl);
    }
    
    protected void sleep(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
        }
    }
    
    protected Set<Object> mocksToVerify;
    
    protected void setup() throws UnknownHostException, MongoException {
        mocksToVerify = new HashSet<Object>();
    }
    
    protected void teardown() {
        if (!mocksToVerify.isEmpty()) {
            verify(mocksToVerify.toArray(new Object[mocksToVerify.size()]));
        }
    }
    
    protected <T> T regMock(T mock) {
        mocksToVerify.add(mock);
        replay(mock);
        return mock;
    }

    protected enum RequestExpect {
        NO_COOKIES, WITH_COOKIE, WITH_REMOTE_ADDR;
    }
    
    protected HttpServletRequest mockRequest(RequestExpect expect) {
        return mockRequest(expect, null);
    }
    
    protected HttpServletRequest mockRequest(RequestExpect expect,
                                           final Cookie givenCookie) {
        HttpServletRequest request = createStrictMock(HttpServletRequest.class);
        expect(request.getContextPath()).andReturn("");
        if (expect == RequestExpect.WITH_REMOTE_ADDR) {
            expect(request.getRemoteAddr()).andReturn(LOCALHOST);
        } else {
            expect(request.getRequestURI()).andReturn("/");
            expect(request.getQueryString()).andReturn(null);
            if (expect == RequestExpect.WITH_COOKIE) {
                Cookie cookie = givenCookie != null ? givenCookie : new Cookie("session", FOOBAR);
                expect(request.getCookies()).andReturn(new Cookie[] {cookie});
            } if (expect == RequestExpect.NO_COOKIES) {
                expect(request.getCookies()).andReturn(null);
            }
        }
        return regMock(request);
    }

}