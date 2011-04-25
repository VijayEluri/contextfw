package net.contextfw.web.application.properties;

public class ClassProperty<S> extends BaseProperty<Class<? extends S>> 
   implements SettableProperty<Class<? extends S>> {

    public ClassProperty(String key) {
        super(key);
    }

    @Override
    public Class<? extends S> unserialize(String value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String serialize(Class<? extends S> value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<? extends S> validate(Class<? extends S> value) {
        return value;
    }
}
