package net.contextfw.web.application.internal.configuration;

import net.contextfw.web.application.configuration.SelfKeyValueSetProperty;



public class SelfKeyValueSetPropertyImpl<K, V> extends SelfSetPropertyImpl<KeyValue<K, V>> 
    implements SelfKeyValueSetProperty<K, V> {

    public SelfKeyValueSetPropertyImpl(String key) {
        super(key);
    }
    
    private SelfKeyValueSetPropertyImpl(String key, KeyValue<K, V> value) {
        super(key, value);
    }
    
    /* (non-Javadoc)
     * @see net.contextfw.web.application.internal.configuration.SelfKeyValueSetProperty#as(K, V)
     */
    @Override
    public SelfKeyValueSetProperty<K, V> as (K key, V value) {
        return new SelfKeyValueSetPropertyImpl<K, V>(getKey(), new KeyValue<K, V>(key, value));
    }
}
