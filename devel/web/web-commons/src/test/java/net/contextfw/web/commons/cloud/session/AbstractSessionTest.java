package net.contextfw.web.commons.cloud.session;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.HttpContext;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.commons.AbstractGuiceTest;
import net.contextfw.web.commons.GuiceJUnitRunner.GuiceModules;
import net.contextfw.web.commons.cloud.serializer.Serializer;

import org.junit.After;
import org.junit.Before;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

@GuiceModules({SessionTestModule.class })
public abstract class AbstractSessionTest extends AbstractGuiceTest {

    protected static final String FOOBAR = "foobar";

    protected static final String COOKIE_NAME = "cloudSession";
    
    protected enum RequestExpect {
        NO_COOKIES, WITH_COOKIE;
    }
    
    protected enum ResponseExpect {
        ADD_COOKIE, ADD_COOKIE_TWICE
    }
    
    @Inject
    protected Configuration conf;
    
    @Inject
    protected Serializer serializer;
    
    protected Set<Object> mocksToVerify;
    
    @Before
    public void setup() throws UnknownHostException, MongoException {
        Mongo mongo = new Mongo();
        mongo.dropDatabase(SessionTestModule.TEST_DB);
        db = mongo.getDB(SessionTestModule.TEST_DB);
        mocksToVerify = new HashSet<Object>();
    }
    
    @After
    public void teardown() {
        if (!mocksToVerify.isEmpty()) {
            verify(mocksToVerify.toArray(new Object[mocksToVerify.size()]));
        }
    }
    
    protected DB db;
    
    protected <T> T regMock(T mock) {
        mocksToVerify.add(mock);
        replay(mock);
        return mock;
    }

    protected HttpServletRequest mockRequest(RequestExpect expect) {
        return mockRequest(expect, null);
    }
    
    protected HttpServletRequest mockRequest(RequestExpect expect,
                                           final Cookie givenCookie) {
        HttpServletRequest request = createStrictMock(HttpServletRequest.class);
        expect(request.getRequestURI()).andReturn("/");
        expect(request.getQueryString()).andReturn(null);
        if (expect == RequestExpect.WITH_COOKIE) {
            Cookie cookie = givenCookie != null ? givenCookie : new Cookie("session", FOOBAR);
            expect(request.getCookies()).andReturn(new Cookie[] {cookie});
        } if (expect == RequestExpect.NO_COOKIES) {
            expect(request.getCookies()).andReturn(null);
        }
        return regMock(request);
    }
    
    protected HttpServletResponse mockResponse(ResponseExpect expect) {
        return mockResponse(expect, null);
    }
    
    protected HttpServletResponse mockResponse(ResponseExpect expect, Cookie cookie) {
        HttpServletResponse response = createStrictMock(HttpServletResponse.class);
        
        if (expect == ResponseExpect.ADD_COOKIE || expect == ResponseExpect.ADD_COOKIE_TWICE) {
            response.addCookie(cookie != null ? cookie : anyObject(Cookie.class));
        } 
        if (expect == ResponseExpect.ADD_COOKIE_TWICE) {
            response.addCookie(cookie != null ? cookie : anyObject(Cookie.class));
        }
        
        return regMock(response);
    }
    
    protected Provider<HttpContext> mockHttpContext(
            HttpServletRequest request, 
            HttpServletResponse response) {
        
        final HttpContext context = new HttpContext(null, request, response);
        
        Provider<HttpContext> provider = new Provider<HttpContext>() {
            @Override
            public HttpContext get() {
                return context;
            }
        };
        
        return provider;
    }
    
    protected Provider<CloudSessionHolder> mockSessionHolder(String handle) {
        
        final CloudSessionHolder holder = new CloudSessionHolder();
        
        holder.setHandle(handle);
        
        Provider<CloudSessionHolder> provider = new Provider<CloudSessionHolder>() {
            @Override
            public CloudSessionHolder get() {
                return holder;
            }
        };
        
        return provider;
    }
    
    protected CloudSession getBasicSession(
            Provider<HttpContext> httpContext,
            Provider<CloudSessionHolder> sessionHolder) {
        MongoCloudSession session =  
                new MongoCloudSession(db,
                                      conf, 
                                      httpContext,
                                      sessionHolder,
                                      serializer);
        
        return session;
    }
    
    protected void mockSession() {
        
        DBObject obj = BasicDBObjectBuilder
                .start()
                .add("handle", FOOBAR)
                .add("validThrough", System.currentTimeMillis() + 1000)
                .add("locked", false)
                .get();
        
        db.getCollection(SessionTestModule.TEST_COLLECTION).insert(obj);
    }
    
}
