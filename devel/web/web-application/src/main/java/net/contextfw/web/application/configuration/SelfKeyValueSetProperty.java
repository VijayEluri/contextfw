package net.contextfw.web.application.configuration;


public class SelfKeyValueSetProperty<K, V> extends SelfSetProperty<KeyValue<K, V>> {

    public SelfKeyValueSetProperty(String key) {
        super(key);
    }
    
    private SelfKeyValueSetProperty(String key, KeyValue<K, V> value) {
        super(key, value);
    }
    
    public SelfKeyValueSetProperty<K, V> as (K key, V value) {
        return new SelfKeyValueSetProperty<K, V>(getKey(), new KeyValue<K, V>(key, value));
    }
}
