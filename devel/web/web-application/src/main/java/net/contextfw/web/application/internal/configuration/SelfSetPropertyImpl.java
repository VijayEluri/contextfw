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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.contextfw.web.application.configuration.SelfAddableProperty;

public class SelfSetPropertyImpl<V> extends BaseProperty<Set<V>> implements SelfAddableProperty<Set<V>, V> {

    private final V value;
    
    public SelfSetPropertyImpl(String key) {
        super(key);
        this.value = null;
    }
    
    public SelfSetPropertyImpl(String key, V value) {
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
