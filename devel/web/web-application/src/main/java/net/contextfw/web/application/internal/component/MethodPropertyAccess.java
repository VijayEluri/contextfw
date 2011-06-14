package net.contextfw.web.application.internal.component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.internal.InternalWebApplicationException;

final class MethodPropertyAccess implements PropertyAccess<Object> {

    private final Method method;
    
    public MethodPropertyAccess(Method method) {
        if (method.getParameterTypes().length > 0) {
            throw new WebApplicationException("Method " + method.getDeclaringClass().getName() 
                    + "." + method.getName() + "() cannot take any parameters");
        }
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