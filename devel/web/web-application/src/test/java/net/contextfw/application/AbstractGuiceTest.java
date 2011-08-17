package net.contextfw.application;

import net.contextfw.application.GuiceJUnitRunner.GuiceModules;
import net.contextfw.web.application.TestModule;

import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Injector;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({TestModule.class })
public abstract class AbstractGuiceTest extends AbstractTest {

    @Inject
    private Injector injector;
    
    protected <T> T getMember(Class<T> clazz) {
        return injector.getInstance(clazz);
    }
    
    protected <T> T injectMembers(T instance) {
        injector.injectMembers(instance);
        return instance;
    }
}
