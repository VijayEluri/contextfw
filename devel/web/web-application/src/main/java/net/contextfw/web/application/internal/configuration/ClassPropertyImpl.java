package net.contextfw.web.application.internal.configuration;

import net.contextfw.web.application.configuration.SettableProperty;

public class ClassPropertyImpl<S> extends BaseProperty<Class<? extends S>> 
   implements SettableProperty<Class<? extends S>> {

    public ClassPropertyImpl(String key) {
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
