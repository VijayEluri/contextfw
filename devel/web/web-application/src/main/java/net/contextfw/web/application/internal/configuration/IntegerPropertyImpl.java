package net.contextfw.web.application.internal.configuration;

import net.contextfw.web.application.configuration.SettableProperty;

public class IntegerPropertyImpl extends BaseProperty<Integer> 
    implements SettableProperty<Integer> {

    public IntegerPropertyImpl(String key) {
        super(key);
    }

    @Override
    public Integer unserialize(String value) {
        return value == null ? null : Integer.parseInt(value);
    }

    @Override
    public String serialize(Integer value) {
        return value == null ? null : value.toString();
    }

    @Override
    public Integer validate(Integer value) {
        return value;
    }
}
