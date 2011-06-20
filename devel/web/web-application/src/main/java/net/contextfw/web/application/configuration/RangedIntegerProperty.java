package net.contextfw.web.application.configuration;

public class RangedIntegerProperty extends IntegerProperty {

    private final int min;
    private final int max;
    
    public RangedIntegerProperty(String key, int min, int max) {
        super(key);
        this.min = min;
        this.max = max;
    }

    @Override
    public Integer validate(Integer value) {
        if (value == null) {
            throw new IllegalArgumentException("Property '"+getKey()+"' cannot be null");
        }
        if (value < min) {
            throw new IllegalArgumentException("Property '"+getKey()+"' was too small: " 
                    + value + " < " + min);
        }
        if (value > max) {
            throw new IllegalArgumentException("Property '"+getKey()+"' was too big: " 
                    + value + " > " + max);
        }
        return value;
    }
}
