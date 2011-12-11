package net.contextfw.web.commons.cloud.session;

import static org.easymock.EasyMock.anyObject;

import java.net.UnknownHostException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.HttpContext;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.commons.AbstractGuiceTest;
import net.contextfw.web.commons.GuiceJUnitRunner.GuiceModules;
import net.contextfw.web.commons.cloud.serializer.Serializer;

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

    protected static final String COOKIE_NAME = "cloudSession";
    
    protected enum ResponseExpect {
        ADD_COOKIE, ADD_COOKIE_TWICE, ADD_COOKIE_THRICE
    }
    
    @Inject
    protected Configuration conf;
    
    @Inject
    protected Serializer serializer;
    
    @Before
    public void setup() throws UnknownHostException, MongoException {
        super.setup();
        Mongo mongo = new Mongo();
        mongo.dropDatabase(SessionTestModule.TEST_DB);
        db = mongo.getDB(SessionTestModule.TEST_DB);
    }
    
    protected DB db;
        
    protected HttpServletResponse mockResponse(ResponseExpect expect) {
        return mockResponse(expect, null);
    }
    
    protected HttpServletResponse mockResponse(ResponseExpect expect, Cookie cookie) {
        HttpServletResponse response = createStrictMock(HttpServletResponse.class);
        
        if (expect == ResponseExpect.ADD_COOKIE 
                || expect == ResponseExpect.ADD_COOKIE_TWICE
                || expect == ResponseExpect.ADD_COOKIE_THRICE) {
            
            response.addCookie(cookie != null ? cookie : anyObject(Cookie.class));
        } 
        if (expect == ResponseExpect.ADD_COOKIE_TWICE
                || expect == ResponseExpect.ADD_COOKIE_THRICE) {
            response.addCookie(cookie != null ? cookie : anyObject(Cookie.class));
        }
        if (expect == ResponseExpect.ADD_COOKIE_THRICE) {
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
