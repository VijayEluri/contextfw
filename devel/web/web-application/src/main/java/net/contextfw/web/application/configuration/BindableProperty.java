package net.contextfw.web.application.configuration;

import net.contextfw.web.application.internal.configuration.SelfSettableProperty;

public interface BindableProperty<T> extends SelfSettableProperty<Object> {
    
    <S extends T> BindableProperty<S> as(Class<S> type);
    
    BindableProperty<T> asInstance(T instance);
}
