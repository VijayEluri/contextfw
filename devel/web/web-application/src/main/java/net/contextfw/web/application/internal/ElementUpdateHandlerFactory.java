package net.contextfw.web.application.internal;

import java.lang.reflect.Method;

import net.contextfw.web.application.annotations.RemoteMethod;
import net.contextfw.web.application.elements.CElement;

import com.google.inject.Singleton;

@Singleton
public class ElementUpdateHandlerFactory {

    public ElementUpdateHandler createHandler(Class<? extends CElement> elClass, String methodName) {

        Class<?> cls = elClass;
        Method method = null;

        while (CElement.class.isAssignableFrom(cls) && method == null) {
            method = findMethod(cls, methodName);
            cls = cls.getSuperclass();
        }

        if (method != null) {
            return new ElementUpdateHandler(ElementUpdateHandler.getKey(elClass, methodName), method);
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