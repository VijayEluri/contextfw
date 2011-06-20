package net.contextfw.web.application.configuration;

public interface SelfSettableProperty<T> extends Property<T> {
    T getValue();
}
