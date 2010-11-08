package net.contextfw.web.application.internal.component;

import java.lang.reflect.Field;

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
            throw new InternalWebApplicationException(e);
        } catch (IllegalAccessException e) {
            throw new InternalWebApplicationException(e);
        }
    }
}