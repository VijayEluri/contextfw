package net.contextfw.web.application.internal.component;

import java.lang.reflect.Field;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.internal.InternalWebApplicationException;

final class FieldPropertyAccess<T> implements PropertyAccess<T> {

    private final Field field;
    
    public FieldPropertyAccess(Field field) {
        this.field = field;
        field.setAccessible(true);
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