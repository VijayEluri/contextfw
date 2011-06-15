package net.contextfw.web.application.properties;

public class BindableProperty<T> implements SelfSettableProperty<Object> {

    private final String key;
    
    private final T instance;
   
    private final Class<T> type;
    
    @Override
    public String getKey() {
        return key;
    }
    
    public BindableProperty(String key) {
        this.key = key;
        this.instance = null;
        this.type = null;
    }

    private BindableProperty(String key, T instance) {
        this.key = key;
        this.instance = instance;
        this.type = null;
    }
    
    private BindableProperty(String key, Class<T> type) {
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
        return new BindableProperty<S>(key, type);
    }
    
    public BindableProperty<T> asInstance(T instance) {
        return new BindableProperty<T>(key, instance);
    }
}
