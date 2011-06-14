package net.contextfw.web.application.properties;

public class ObjectProperty<T> extends BaseProperty<T> 
   implements SettableProperty<T> {

    public ObjectProperty(String key) {
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
