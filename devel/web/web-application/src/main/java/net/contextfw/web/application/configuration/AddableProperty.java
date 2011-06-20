package net.contextfw.web.application.configuration;

import java.util.Collection;

public interface AddableProperty<T extends Collection<V>, V> extends Property<T> {
    T add(T collection, V value);
}

