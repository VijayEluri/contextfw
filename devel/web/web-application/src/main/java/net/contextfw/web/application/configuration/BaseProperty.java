package net.contextfw.web.application.configuration;

public abstract class BaseProperty<T> {

    protected BaseProperty(String key) {
        this.key = key;
    }

    private final String key;
    
    public String getKey() {
        return key;
    }
}
