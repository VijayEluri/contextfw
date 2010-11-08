package net.contextfw.web.application.internal.component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.contextfw.web.application.internal.InternalWebApplicationException;

final class MethodPropertyAccess implements PropertyAccess<Object> {

    private final Method method;
    
    public MethodPropertyAccess(Method method) {
        this.method = method;
    }

    @Override
    public Object getValue(Object obj) {
         try {
            return method.invoke(obj);
        } catch (IllegalArgumentException e) {
            throw new InternalWebApplicationException(e);
        } catch (IllegalAccessException e) {
            throw new InternalWebApplicationException(e);
        } catch (InvocationTargetException e) {
            throw new InternalWebApplicationException(e);
        }
    }
}