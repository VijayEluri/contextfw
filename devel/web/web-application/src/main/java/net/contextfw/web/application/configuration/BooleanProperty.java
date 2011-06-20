package net.contextfw.web.application.configuration;

public class BooleanProperty extends BaseProperty<Boolean> 
   implements SettableProperty<Boolean> {

    public BooleanProperty(String key) {
        super(key);
    }

    @Override
    public Boolean unserialize(String value) {
        return value == null ? null : Boolean.parseBoolean(value);
    }

    @Override
    public String serialize(Boolean value) {
        return value == null ? null : value.toString();
    }

    @Override
    public Boolean validate(Boolean value) {
        return value;
    }
}
