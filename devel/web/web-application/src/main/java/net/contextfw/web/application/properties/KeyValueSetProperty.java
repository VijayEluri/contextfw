package net.contextfw.web.application.properties;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class KeyValueSetProperty<K, V> extends BaseProperty<Set<KeyValue<K, V>>> 
    implements AppendableProperty<Set<KeyValue<K, V>>, KeyValue<K, V>> {

    private final KeyValue<K, V> value;
    
    public KeyValueSetProperty(String key) {
        super(key);
        value = null;
    }
    
    private KeyValueSetProperty(String key, KeyValue<K, V> value) {
        super(key);
        this.value = value;
    }

    @Override
    public String serialize(Collection<KeyValue<K, V>> value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<KeyValue<K, V>> validate(Collection<KeyValue<K, V>> value) {
        return Collections.unmodifiableCollection(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<KeyValue<K, V>> get(Collection<KeyValue<K, V>> value) {
        return (Collection<KeyValue<K, V>>) (value == null ? Collections.emptySet() : 
            Collections.unmodifiableCollection(value));
    }

    @Override
    public Set<KeyValue<K, V>> append(Collection<KeyValue<K, V>> collection, KeyValue<K, V> value) {
        Set<KeyValue<K, V>> rv = new HashSet<KeyValue<K, V>>();
        if (collection != null) {
            rv.addAll(collection);
        }
        rv.add(value);
        return rv;
    }

    @Override
    public Collection<KeyValue<K, V>> unserialize(String value) {
        return null;
    }
    
    public KeyValueSetProperty<K, V> as(K key, V value) {
        return new KeyValueSetProperty<K, V>(this.getKey(), new KeyValue<K, V>(key, value));
    }

    public KeyValue<K, V> getValue() {
        return value;
    }
}
