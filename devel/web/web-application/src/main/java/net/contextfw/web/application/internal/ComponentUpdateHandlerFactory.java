package net.contextfw.web.application.internal;

import java.lang.reflect.Method;
import java.util.Map.Entry;

import net.contextfw.web.application.ModuleConfiguration;
import net.contextfw.web.application.annotations.RemoteMethod;
import net.contextfw.web.application.component.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class ComponentUpdateHandlerFactory {
    
    private Gson gson;
    
    @Inject
    public ComponentUpdateHandlerFactory(Injector injector, ModuleConfiguration configuration) {
        
        GsonBuilder builder = new GsonBuilder();
        
        for (Entry<Class<?>, Class<?>> entry: configuration.getJsonSerializerClasses()) {
            builder.registerTypeAdapter(entry.getKey(), injector.getInstance(entry.getValue()));
        }

        for (Entry<Class<?>, Class<?>> entry: configuration.getJsonDeserializerClasses()) {
            builder.registerTypeAdapter(entry.getKey(), injector.getInstance(entry.getValue()));
        }        
        
        gson = builder.create();
    }
    
    public ComponentUpdateHandler createHandler(Class<? extends Component> elClass, String methodName) {

        Class<?> cls = elClass;
        Method method = null;

        while (Component.class.isAssignableFrom(cls) && method == null) {
            method = findMethod(cls, methodName);
            cls = cls.getSuperclass();
        }

        if (method != null) {
            return new ComponentUpdateHandler(ComponentUpdateHandler.getKey(elClass, methodName), method, gson);
        }
        else {
            return null;
        }
    }

    private Method findMethod(Class<?> cls, String methodName) {
        for (Method method : cls.getDeclaredMethods()) {
            if (method.getAnnotation(RemoteMethod.class) != null && method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }
}