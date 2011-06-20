package net.contextfw.web.application.configuration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SelfSetProperty<V> extends BaseProperty<Set<V>> implements SelfAddableProperty<Set<V>, V> {

    private final V value;
    
    public SelfSetProperty(String key) {
        super(key);
        this.value = null;
    }
    
    public SelfSetProperty(String key, V value) {
        super(key);
        this.value = value;
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

    @Override
    public V getValue() {
        return value;
    }
}
