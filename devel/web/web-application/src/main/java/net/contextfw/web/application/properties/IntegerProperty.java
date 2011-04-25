package net.contextfw.web.application.properties;

public class IntegerProperty extends BaseProperty<Integer> 
    implements SettableProperty<Integer> {

    public IntegerProperty(String key) {
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
