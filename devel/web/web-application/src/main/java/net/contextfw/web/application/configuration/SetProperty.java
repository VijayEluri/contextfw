package net.contextfw.web.application.configuration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SetProperty<V> extends BaseProperty<Set<V>> implements AddableProperty<Set<V>, V> {

    public SetProperty(String key) {
        super(key);
    }

    @Override
    public Set<V> unserialize(String value) {
        return null;
    }

    @Override
    public String serialize(Set<V> value) {
        return null;
    }

    @Override
    public Set<V> validate(Set<V> value) {
        if (value == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(value);
        }
    }

    @Override
    public Set<V> add(Set<V> collection, V value) {
        Set<V> rv = new HashSet<V>();
        if (collection != null) {
            rv.addAll(collection);
        }
        rv.add(value);
        return rv;
    }
}
