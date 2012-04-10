package net.contextfw.web.commons.cloud.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import net.contextfw.web.application.WebApplication;
import net.contextfw.web.application.PageHandle;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.scope.ScopedWebApplicationExecution;
import net.contextfw.web.application.scope.WebApplicationStorage;
import net.contextfw.web.commons.cloud.internal.serializer.Serializer;

import org.apache.commons.lang.mutable.MutableBoolean;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

public class BasicStorageTest extends AbstractStorageTest {

    private static final String I_WAS_CHANGED = "I was changed";

    private static final String SCOPED1 = "Scoped1";

    @Inject
    private SingletonScoped singletonScoped;
    
    @Inject
    Provider<WebApplicationMock> webApplicationProvider;

    @Inject
    protected Serializer serializer;
    
    @Inject
    protected Configuration configuration;
    
    private WebApplicationStorage storage;
    
    private WebApplicationStorage getStorage() {
        return new MongoWebApplicationStorage(db, configuration, serializer);
    }
    
    @Before
    public void setupStorage() {
        storage = getStorage();
    }
    
    private ScopedWebApplicationExecution mockExecution(WebApplication application) {
        
        ScopedWebApplicationExecution mock = 
                createStrictMock(ScopedWebApplicationExecution.class);
        mock.execute(application);
        regMock(mock);
        return mock;
    }
    
    private PageHandle initWebApplication() {
        return initWebApplication(System.currentTimeMillis() + 1100);
    }
    
    private PageHandle initWebApplication(long initialMaxInactivity) {
        WebApplicationMock app = webApplicationProvider.get();
        app.getScoped1().setMsg(SCOPED1);
        app.getScoped2().setMsg("Scoped2");
        storage.initialize(app, 
                           LOCALHOST, 
                           initialMaxInactivity,
                           mockExecution(app));
        return app.getHandle();
    }
    
    @Test
    public void WebApplication_Is_Initialized() {

        long initialMaxInactivity = System.currentTimeMillis() + 1100;
        long maxInactivity = initialMaxInactivity - 500;
        
        final PageHandle handle = initWebApplication(initialMaxInactivity);
        assertNotNull(handle);
        BasicDBObjectBuilder b = new BasicDBObjectBuilder();
        b.add("handle", handle.toString());
        DBObject obj = db.getCollection("pages").findOne(b.get());
        assertNotNull(obj);
        assertEquals(initialMaxInactivity, obj.get("validThrough"));
        
        storage.refresh(handle, LOCALHOST, maxInactivity);
        obj = db.getCollection("pages").findOne(b.get());
        assertNotNull(obj);
        assertEquals(maxInactivity, obj.get("validThrough"));

        final MutableBoolean executionCalled = new MutableBoolean(false);
        
        ScopedWebApplicationExecution execution = new ScopedWebApplicationExecution() {
            public void execute(WebApplication application) {
                WebApplicationMock mock = (WebApplicationMock) application;
                assertSame(singletonScoped, mock.getSingletonScoped());
                assertNotNull(mock.getScoped1());
                assertNotNull(mock.getScoped2());
                assertEquals(SCOPED1, mock.getScoped1().getMsg());
                assertNull(mock.getScoped2().getMsg());
                assertEquals(handle, mock.getHandle());
                executionCalled.setValue(true);
            }
        };
        
        storage.update(handle, 
                        LOCALHOST, 
                        System.currentTimeMillis() + 1000,
                        execution);
        
        assertTrue(executionCalled.booleanValue());
    }
    
    @Test
    public void WebApplication_Is_Removed() {
        
        final PageHandle handle = initWebApplication();
        assertNotNull(handle);

        final MutableBoolean executionCalled = new MutableBoolean(false);
        
        ScopedWebApplicationExecution execution = new ScopedWebApplicationExecution() {
            public void execute(WebApplication application) {
                assertNull(application);
                executionCalled.setValue(true);
            }
        };
        
        storage.remove(handle, LOCALHOST);
        
        storage.update(handle, 
                        LOCALHOST, 
                        System.currentTimeMillis() + 1000,
                        execution);
        
        assertTrue(executionCalled.booleanValue());
    }
    
    @Test
    public void Execute_And_Update_With_Existing_WebApplication() {
        
        final PageHandle handle = initWebApplication();
        
        ScopedWebApplicationExecution changeExecution = new ScopedWebApplicationExecution() {
            public void execute(WebApplication application) {
                assertNotNull(application);
                WebApplicationMock mock = (WebApplicationMock) application;
                mock.getScoped1().setMsg(I_WAS_CHANGED);
                mock.getScoped1().addItem("Item1");
            }
        };
        
        storage.execute(handle, changeExecution);
        
        final MutableBoolean executionCalled = new MutableBoolean(false);
        
        ScopedWebApplicationExecution verifyExecution = new ScopedWebApplicationExecution() {
            public void execute(WebApplication application) {
                WebApplicationMock mock = (WebApplicationMock) application;
                assertEquals(I_WAS_CHANGED, mock.getScoped1().getMsg());
                assertEquals("Item1", mock.getScoped1().getItem(0));
                executionCalled.setValue(true);
            }
        };
        
        storage.execute(handle, verifyExecution);
        
        assertTrue(executionCalled.booleanValue());
    }
    
    @Test
    public void Execute_With_Non_Exisiting_WebApplication() {
        
        final MutableBoolean executionCalled = new MutableBoolean(false);
        
        ScopedWebApplicationExecution execution = new ScopedWebApplicationExecution() {
            public void execute(WebApplication application) {
                assertNull(application);
                executionCalled.setValue(true);
            }
        };
        
        storage.execute(new PageHandle("foo"), execution);
        
        assertTrue(executionCalled.booleanValue());
    }

    @Test
    public void WebApplication_Is_Refreshed() {
        
        final PageHandle handle = initWebApplication();
        
        sleep(500);
        
        storage.refresh(handle,
                LOCALHOST, 
                System.currentTimeMillis() + 1500);
        
        sleep(1000);
        
        final MutableBoolean executionCalled = new MutableBoolean(false);
        
        ScopedWebApplicationExecution execution = new ScopedWebApplicationExecution() {
            public void execute(WebApplication application) {
                assertNotNull(application);
                executionCalled.setValue(true);
            }
        };
        
        storage.execute(handle, execution);
        
        assertTrue(executionCalled.booleanValue());
        
    }
    
    @Test
    public void WebApplication_Is_Expired() {
        
        final PageHandle handle = initWebApplication();
        
        final MutableBoolean executionCalled = new MutableBoolean(false);
        
        ScopedWebApplicationExecution execution = new ScopedWebApplicationExecution() {
            public void execute(WebApplication application) {
                assertNull(application);
                executionCalled.setValue(true);
            }
        };
        
        sleep(1500);
        
        storage.execute(handle, execution);
        
        assertTrue(executionCalled.booleanValue());
    }
}
