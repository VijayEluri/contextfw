package net.contextfw.web.application.configuration;

import java.util.Set;

import net.contextfw.web.application.internal.configuration.KeyValue;

public interface SelfKeyValueSetProperty<K, V> extends SelfAddableProperty<Set<KeyValue<K,V>>, KeyValue<K,V>> {

    SelfKeyValueSetProperty<K, V> as(K key, V value);

}