package net.contextfw.web.application.internal.component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.internal.InternalWebApplicationException;

final class MethodPropertyAccess implements PropertyAccess<Object> {

    private final Method method;
    
    public MethodPropertyAccess(final Method method) {
        if (method.getParameterTypes().length > 0) {
            throw new WebApplicationException("Method " + method.getDeclaringClass().getName() 
                    + "." + method.getName() + "() cannot take any parameters");
        }
        this.method = method;
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                method.setAccessible(true);
                return null;
            }
        });
    }

    @Override
    public Object getValue(Object obj) {
         try {
            return method.invoke(obj);
        } catch (IllegalArgumentException e) {
            if (WebApplicationException.class.isAssignableFrom(e.getCause().getClass())) {
                throw (RuntimeException) e.getCause();
            } else {
                throw new InternalWebApplicationException(e);
            }
        } catch (IllegalAccessException e) {
            if (WebApplicationException.class.isAssignableFrom(e.getCause().getClass())) {
                throw (RuntimeException) e.getCause();
            } else {
                throw new InternalWebApplicationException(e);
            }
        } catch (InvocationTargetException e) {
            if (WebApplicationException.class.isAssignableFrom(e.getCause().getClass())) {
                throw (RuntimeException) e.getCause();
            } else {
                throw new InternalWebApplicationException(e);
            }
        }
    }
}