package net.contextfw.web.application.internal.configuration;

import net.contextfw.web.application.configuration.SettableProperty;

public class BasicSettableProperty<T> extends BaseProperty<T> implements SettableProperty<T>  {

    public BasicSettableProperty(String key) {
        super(key);
    }
    
    @Override
    public T validate(T value) {
        return value;
    }
}
