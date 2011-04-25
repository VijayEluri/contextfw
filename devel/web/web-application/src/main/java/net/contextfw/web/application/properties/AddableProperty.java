package net.contextfw.web.application.properties;

import java.util.Collection;

public interface AddableProperty<T extends Collection<V>, V> extends Property<T> {
    T add(T collection, V value);
}

