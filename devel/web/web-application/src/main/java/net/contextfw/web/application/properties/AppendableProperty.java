package net.contextfw.web.application.properties;

import java.util.Collection;

public interface AppendableProperty<C extends Collection<T>, T> extends Property<Collection<T>> {

    C append(Collection<T> collection, T value);
}

