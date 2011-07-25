package net.contextfw.web.application.internal.configuration;


public interface SelfSettableProperty<T> extends Property<T> {
    T getValue();
}
