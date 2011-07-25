package net.contextfw.web.application.internal.configuration;

import net.contextfw.web.application.configuration.SettableProperty;

public class StringPropertyImpl extends BaseProperty<String> implements SettableProperty<String> {

    public StringPropertyImpl(String key) {
        super(key);
    }

    @Override
    public String unserialize(String value) {
        return value;
    }

    @Override
    public String serialize(String value) {
        return value;
    }

    @Override
    public String validate(String value) {
        return value;
    }
}
