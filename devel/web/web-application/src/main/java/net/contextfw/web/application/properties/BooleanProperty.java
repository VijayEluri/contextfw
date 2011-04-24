package net.contextfw.web.application.properties;

public class BooleanProperty extends BaseProperty<Boolean> 
   implements Property<Boolean> {

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

    @Override
    public Boolean get(Boolean value) {
        return value;
    }
}
