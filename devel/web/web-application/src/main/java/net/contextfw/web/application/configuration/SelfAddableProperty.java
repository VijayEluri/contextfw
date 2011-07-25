package net.contextfw.web.application.configuration;

import java.util.Collection;

import net.contextfw.web.application.internal.configuration.Property;

public interface SelfAddableProperty<T extends Collection<V>, V> extends Property<T> {
    T add(T collection, V value);
    V getValue();
}

