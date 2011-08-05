package net.contextfw.web.application.internal.component;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.internal.InternalWebApplicationException;

final class FieldPropertyAccess<T> implements PropertyAccess<T> {

    private final Field field;
    
    public FieldPropertyAccess(final Field field) {
        this.field = field;
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                field.setAccessible(true);
                return null;
            }
        });
        
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getValue(Object obj) {
         try {
            return (T) field.get(obj);
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
        }
    }
}