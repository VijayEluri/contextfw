package net.contextfw.web.application.internal.configuration;

import net.contextfw.web.application.configuration.BindableProperty;

public class BindablePropertyImpl<T> implements BindableProperty<T> {

    private final String key;
    
    private final T instance;
   
    private final Class<T> type;
    
    @Override
    public String getKey() {
        return key;
    }
    
    public BindablePropertyImpl(String key) {
        this.key = key;
        this.instance = null;
        this.type = null;
    }

    private BindablePropertyImpl(String key, T instance) {
        this.key = key;
        this.instance = instance;
        this.type = null;
    }
    
    private BindablePropertyImpl(String key, Class<T> type) {
        this.key = key;
        this.instance = null;
        this.type = type;
    }

    @Override
    public Object unserialize(String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String serialize(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object validate(Object value) {
        return value;
    }

    @Override
    public Object getValue() {
        return instance == null ? type : instance; 
    }

    public T getInstance() {
        return instance;
    }

    public Class<T> getType() {
        return type;
    }
    
    public <S extends T> BindableProperty<S> as(Class<S> type) {
        return new BindablePropertyImpl<S>(key, type);
    }
    
    public BindableProperty<T> asInstance(T instance) {
        return new BindablePropertyImpl<T>(key, instance);
    }
}
