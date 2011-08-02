package net.contextfw.web.application.internal;

import java.lang.reflect.Method;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.internal.util.ClassScanner;
import net.contextfw.web.application.lifecycle.LifecycleListener;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class ComponentUpdateHandlerFactory {
    
    private Gson gson;
    
    private final LifecycleListener listener;
    
    @Inject
    public ComponentUpdateHandlerFactory(Injector injector, Gson gson, LifecycleListener listener) {
        this.gson = gson;
        this.listener = listener;
    }
    
    public ComponentUpdateHandler createHandler(Class<? extends Component> elClass, String methodName) {

        Method method = ClassScanner.findMethodForName(elClass, methodName);

        if (method != null) {
            return new ComponentUpdateHandler(ComponentUpdateHandler.getKey(elClass, methodName), method, gson, listener);
        }
        else {
            return null;
        }
    }
}