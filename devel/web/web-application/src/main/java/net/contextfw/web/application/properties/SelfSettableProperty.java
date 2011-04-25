package net.contextfw.web.application.properties;

public interface SelfSettableProperty<T> extends Property<T> {
    T getValue();
}
