package net.contextfw.web.commons.cloud.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import net.contextfw.web.application.WebApplication;
import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.PageHandle;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.scope.ScopedWebApplicationExecution;
import net.contextfw.web.application.scope.WebApplicationStorage;
import net.contextfw.web.commons.cloud.internal.serializer.Serializer;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class LargeObjectTest extends AbstractStorageTest {

    private static final String SCOPED1 = "Scoped1";

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
        WebApplicationMock app = webApplicationProvider.get();
        app.getScoped1().setMsg(SCOPED1);
        app.getScoped2().setMsg("Scoped2");
        storage.initialize(app, 
                           LOCALHOST, 
                           System.currentTimeMillis() + 1100,
                           mockExecution(app));
        return app.getHandle();
    }
    
    @Test
    public void Store_And_Load_Large() {
        final PageHandle handle = initWebApplication();
        Long obj = new Long(1);
        storage.storeLarge(handle, "test", obj);
        assertEquals(obj, storage.loadLarge(handle, "test", Long.class));
        storage.storeLarge(handle, "test", null);
        assertNull(storage.loadLarge(handle, "test", Long.class));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void Store_Large_Null_Handle() {
        storage.storeLarge(null, "test", "test");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void Store_Large_Null_Key() {
        final PageHandle handle = initWebApplication();
        storage.storeLarge(handle, null, "test");
    }
    
    @Test(expected=WebApplicationException.class)
    public void Store_Large_Non_Existent_Scope() {
        storage.storeLarge(new PageHandle("foo"), "test", "test");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void Load_Large_Null_Handle() {
        storage.loadLarge(null, "test", Long.class);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void Load_Large_Null_Key() {
        final PageHandle handle = initWebApplication();
        storage.loadLarge(handle, null, Long.class);
    }
    
    @Test(expected=WebApplicationException.class)
    public void Load_Large_Non_Existent_Scope() {
        storage.loadLarge(new PageHandle("foo"), "test", Long.class);
    }
}
