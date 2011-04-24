package net.contextfw.web.application.properties;

public abstract class BaseProperty<T> {

    protected BaseProperty(String key) {
        this.key = key;
    }

    private final String key;
    
    public String getKey() {
        return key;
    }
}
