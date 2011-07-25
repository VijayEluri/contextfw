package net.contextfw.web.application.internal.configuration;

import net.contextfw.web.application.configuration.SettableProperty;

public class ObjectPropertyImpl<T> extends BaseProperty<T> 
   implements SettableProperty<T> {

    public ObjectPropertyImpl(String key) {
        super(key);
    }

    @Override
    public T unserialize(String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String serialize(T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T validate(T value) {
        return value;
    }
}
