/**
 * Copyright 2010 Marko Lavikainen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.contextfw.web.application.internal.configuration;

import net.contextfw.web.application.configuration.BindableProperty;

public class BindablePropertyImpl<T> implements BindableProperty<T> {

    private final String key;
    
    private final T instance;
   
    private final Class<T> type;
    
    @Override
    public String getKey() {
        return key;
    }
    
    public BindablePropertyImpl(String key) {
        this.key = key;
        this.instance = null;
        this.type = null;
    }

    private BindablePropertyImpl(String key, T instance) {
        this.key = key;
        this.instance = instance;
        this.type = null;
    }
    
    private BindablePropertyImpl(String key, Class<T> type) {
        this.key = key;
        this.instance = null;
        this.type = type;
    }

    @Override
    public Object validate(Object value) {
        return value;
    }

    @Override
    public Object getValue() {
        return instance == null ? type : instance; 
    }

    public Class<T> getType() {
        return type;
    }
    
    public <S extends T> BindableProperty<S> as(Class<S> type) {
        return new BindablePropertyImpl<S>(key, type);
    }
    
    public BindableProperty<T> asInstance(T instance) {
        return new BindablePropertyImpl<T>(key, instance);
    }
}
