package net.contextfw.web.application.internal.configuration;

public class KeyValue<K, V> {

    private final K key;

    private final V value;

    public KeyValue(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        this.key = key;
        this.value = value;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other instanceof KeyValue) {
            return this.key.equals(((KeyValue) other).key);
        } else {
            return false;
        }
    }
    
    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }
}
