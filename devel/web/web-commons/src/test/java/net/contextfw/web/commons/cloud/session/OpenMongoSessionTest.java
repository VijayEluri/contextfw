package net.contextfw.web.commons.cloud.session;

import static org.junit.Assert.assertTrue;

import javax.servlet.http.Cookie;

import net.contextfw.web.application.PageContext;
import net.contextfw.web.commons.cloud.internal.session.CloudSessionHolder;

import org.junit.Test;

import com.google.inject.Provider;

public class OpenMongoSessionTest extends AbstractSessionTest {
    
    /**
     * Trying to open existing session without session throws exception
     */
    @Test(expected=NoSessionException.class)
    public void Open_Existing_Session_When_No_Session() {
        
        CloudSession session = getBasicSession(
                mockPageContext(
                        mockRequest(RequestExpect.WITH_COOKIE, null),
                        mockResponse(null)),
                mockSessionHolder(null));
        
        session.openSession(OpenMode.EXISTING);
    }
    
    /**
     * Trying to open existing session without session throws exception
     */
    @Test(expected=NoSessionException.class)
    public void Open_Existing_Session_When_No_Session_2() {
        
        Provider<PageContext> context = mockPageContext(
                mockRequest(null, null), 
                mockResponse(null, null));
        
        Provider<CloudSessionHolder> holder = mockSessionHolder(null);
        
        context.get().setRequest(null);
        context.get().setResponse(null);
        
        CloudSession session = getBasicSession(
                context,
                holder);
        
        session.openSession(OpenMode.EXISTING);
    }
    
    /**
     * Trying to open existing session without session throws exception
     */
    @Test(expected=NoSessionException.class)
    public void Open_Existing_Session_When_No_Session_3() {
        
        Provider<PageContext> context = mockPageContext(
                mockRequest(null, null), 
                mockResponse(null, null));
        
        Provider<CloudSessionHolder> holder = mockSessionHolder(FOOBAR);
        
        context.get().setRequest(null);
        context.get().setResponse(null);
        
        CloudSession session = getBasicSession(
                context,
                holder);
        
        session.openSession(OpenMode.EXISTING);
    }
    
    @Test
    public void Open_Lazy_Session_When_No_Session() {
        CloudSession session = getBasicSession(
                mockPageContext(
                        mockRequest(RequestExpect.WITH_COOKIE),
                        mockResponse(null)),
                mockSessionHolder(null));
        session.openSession(OpenMode.LAZY);
    }
    
    @Test
    public void Open_Eager_Session_When_No_Session() {
        CloudSession session = getBasicSession(
                mockPageContext(
                        mockRequest(RequestExpect.NO_COOKIES), 
                        mockResponse(ResponseExpect.ADD_COOKIE)),
                mockSessionHolder(null));
        session.openSession(OpenMode.EAGER);
    }
    
    @Test
    public void Open_Lazy_Session_With_Session() {
        
        mockSession();
        
        Cookie cookie = new Cookie(COOKIE_NAME, FOOBAR);
        
        CloudSession session = getBasicSession(
                mockPageContext(
                        mockRequest(RequestExpect.WITH_COOKIE, cookie),
                        mockResponse(ResponseExpect.ADD_COOKIE, null)),
                mockSessionHolder(null));
        
        session.openSession(OpenMode.LAZY);
    }
    
    @Test
    public void Open_Lazy_Session_With_Expired_Session() {
        
        mockSession();

        sleep(1100);
        
        Cookie cookie = new Cookie(COOKIE_NAME, FOOBAR);
        
        CloudSession session = getBasicSession(
                mockPageContext(
                        mockRequest(RequestExpect.WITH_COOKIE, cookie),
                        mockResponse(null)),
                mockSessionHolder(null));
        
        session.openSession(OpenMode.LAZY);
    }
    
    @Test
    public void Open_Eager_Session_With_Session() {
        
        Cookie cookie = new Cookie(COOKIE_NAME, FOOBAR);
        
        CloudSession session = getBasicSession(
                mockPageContext(
                        mockRequest(RequestExpect.WITH_COOKIE, cookie), 
                        mockResponse(ResponseExpect.ADD_COOKIE, null)),
                mockSessionHolder(null));
        
        session.openSession(OpenMode.EAGER);
    }
    
    @Test
    public void Open_Existing_Session_With_Session() {
    
        mockSession();
        
        CloudSession session = getBasicSession(
                mockPageContext(
                        mockRequest(null),
                        mockResponse(ResponseExpect.ADD_COOKIE, null)),
                mockSessionHolder(FOOBAR));
        
        session.openSession(OpenMode.EXISTING);
    }
    
    @Test
    public void Open_Existing_Session_With_Session_2() {
    
        mockSession();
        
        Provider<PageContext> context = mockPageContext(
                mockRequest(null, null), 
                mockResponse(null, null));
        
        Provider<CloudSessionHolder> holder = mockSessionHolder(FOOBAR);
        
        context.get().setRequest(null);
        context.get().setResponse(null);
        
        CloudSession session = getBasicSession(
                context,
                holder);
        
        session.openSession(OpenMode.EXISTING);
        
        assertTrue(holder.get().isOpen());
    }
    
    @Test(expected=NoSessionException.class)
    public void Open_Non_Existing_Session_Without_Request() {
    
        mockSession();
        
        Provider<PageContext> context = mockPageContext(
                mockRequest(null, null), 
                mockResponse(null, null));
        
        context.get().setRequest(null);
        context.get().setResponse(null);
        
        CloudSession session = getBasicSession(context, 
                mockSessionHolder(null));
        
        session.openSession(OpenMode.LAZY);
    }
    
    @Test(expected=NoSessionException.class)
    public void Open_Existing_Session_With_Expired_Session() {
    
        mockSession();

        sleep(1100);
        
        Provider<PageContext> context = mockPageContext(
                mockRequest(null, null), 
                mockResponse(null, null));
        
        context.get().setRequest(null);
        context.get().setResponse(null);
        
        CloudSession session = getBasicSession(context, mockSessionHolder(FOOBAR));
        
        session.openSession(OpenMode.EXISTING);
    }
}
