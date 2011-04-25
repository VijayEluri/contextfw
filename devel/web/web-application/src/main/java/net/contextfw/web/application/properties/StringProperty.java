package net.contextfw.web.application.properties;

public class StringProperty extends BaseProperty<String> implements SettableProperty<String> {

    public StringProperty(String key) {
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
