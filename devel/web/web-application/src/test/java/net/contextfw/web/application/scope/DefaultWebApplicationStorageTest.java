package net.contextfw.web.application.scope;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;

import javax.servlet.http.HttpServletRequest;

import net.contextfw.application.AbstractTest;
import net.contextfw.web.application.WebApplication;
import net.contextfw.web.application.WebApplicationHandle;
import net.contextfw.web.application.configuration.Configuration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DefaultWebApplicationStorageTest extends AbstractTest {

    private static final String WRONG_ADDRESS = "10.1.1.10";
    
    private static class SimpleWebApplication implements WebApplication {

        private WebApplicationHandle handle;
        
        @Override
        public void setHandle(WebApplicationHandle handle) {
            this.handle = handle;
        }
    }
    
    private static final String LOCALHOST = "127.0.0.1";
    private static final long MAX_INACTIVITY = 200;
    
    private WebApplicationStorage storage;
    
    private SimpleWebApplication application;
    private HttpServletRequest request;
    private ScopedWebApplicationExecution execution;

    @Before
    public void setup() {
        
        storage = new DefaultWebApplicationStorage(Configuration.getDefaults());
        
        application = new SimpleWebApplication();
        
        request = createStrictMock(HttpServletRequest.class);
        expect(request.getRemoteAddr()).andReturn(LOCALHOST);
        
        execution = createStrictMock(ScopedWebApplicationExecution.class);
        execution.execute(application);
        
        replay(request, execution);
        
        storage.initialize(application, 
                request, 
                System.currentTimeMillis() + MAX_INACTIVITY,
                getClass().getClassLoader(),
                execution);
        
        assertNotNull(application.handle);
    }
    
    
    
    @After
    public void after() {
        verify(request, execution);
    }
    
    @Test
    public void Initialize() {
        // Initialize is implicitly tested during before and after
    }
    
    @Test
    public void Update_From_Correct_Address() {
        ScopedWebApplicationExecution execution = createStrictMock(ScopedWebApplicationExecution.class);
        execution.execute(application);
        replay(execution);
        storage.update(application.handle, 
               getRequest(LOCALHOST),
               System.currentTimeMillis() + 1000, 
               getClass().getClassLoader(),
               execution);
        verify(execution);
    }
    
    @Test
    public void Update_From_Wrong_Address() {
        ScopedWebApplicationExecution execution = createStrictMock(ScopedWebApplicationExecution.class);
        execution.execute(null);
        replay(execution);
        storage.update(application.handle, 
               getRequest(WRONG_ADDRESS),
               System.currentTimeMillis() + 1000,
               getClass().getClassLoader(),
               execution);
        verify(execution);
    }
    
    @Test
    public void Update_Too_Late() {
        sleep(250);
        ScopedWebApplicationExecution execution = createStrictMock(ScopedWebApplicationExecution.class);
        execution.execute(null);
        replay(execution);
        storage.update(application.handle, 
               getRequest(LOCALHOST),
               System.currentTimeMillis() + 1000, getClass().getClassLoader(),
               execution);
        verify(execution);
    }
    
    @Test
    public void Refresh_From_Wrong_Address() {
        
        storage.refresh(application.handle, 
                        getRequest(WRONG_ADDRESS), 
                        System.currentTimeMillis() + 1000);
        sleep(250);
        ScopedWebApplicationExecution execution = createStrictMock(ScopedWebApplicationExecution.class);
        execution.execute(null);
        replay(execution);
        storage.update(application.handle, 
                       getRequest(LOCALHOST), 
                       System.currentTimeMillis() + 1000, 
                       getClass().getClassLoader(),
                       execution);
        verify(execution);
    }
    
    @Test
    public void Remove_From_Wrong_Address() {
        
        storage.refresh(application.handle, 
                getRequest(LOCALHOST), 
                System.currentTimeMillis() + 1000);
        
        storage.remove(application.handle,  getRequest(WRONG_ADDRESS));
        
        ScopedWebApplicationExecution execution = createStrictMock(ScopedWebApplicationExecution.class);
        execution.execute(application);
        replay(execution);
        storage.update(application.handle, 
               getRequest(LOCALHOST),
               System.currentTimeMillis() + 1000, 
               getClass().getClassLoader(),
               execution);
        verify(execution);
    }
    
    @Test
    public void Remove_From_Correct_Address() {
        
        storage.refresh(application.handle, 
                getRequest(LOCALHOST), 
                System.currentTimeMillis() + 1000);
        
        storage.remove(application.handle,  getRequest(LOCALHOST));
        
        ScopedWebApplicationExecution execution = createStrictMock(ScopedWebApplicationExecution.class);
        execution.execute(null);
        replay(execution);
        storage.update(application.handle, 
               getRequest(LOCALHOST),
               System.currentTimeMillis() + 1000, 
               getClass().getClassLoader(),
               execution);
        verify(execution);
    }
    
    @Test
    public void Refresh_From_Correct_Address() {
        storage.refresh(application.handle, 
                        getRequest(LOCALHOST), 
                        System.currentTimeMillis() + 1000);
        sleep(250);
        ScopedWebApplicationExecution execution = createStrictMock(ScopedWebApplicationExecution.class);
        execution.execute(application);
        replay(execution);
        storage.update(application.handle, 
                       getRequest(LOCALHOST),
                       System.currentTimeMillis() + 1000, 
                       getClass().getClassLoader(),
                       execution);
        verify(execution);
    }
    
    @Test
    public void Refresh_Too_Late() {
        
        sleep(250);
        
        storage.refresh(application.handle, 
                        getRequest(LOCALHOST), 
                        System.currentTimeMillis() + 1000);
        ScopedWebApplicationExecution execution = createStrictMock(ScopedWebApplicationExecution.class);
        execution.execute(null);
        replay(execution);
        storage.update(application.handle, 
                       getRequest(LOCALHOST),
                       System.currentTimeMillis() + 1000,
                       getClass().getClassLoader(),
                       execution);
        verify(execution);
    }
    
    private HttpServletRequest getRequest(String remoteAddr) {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        expect(request.getRemoteAddr()).andReturn(remoteAddr);
        replay(request);
        return request;
    }
}
