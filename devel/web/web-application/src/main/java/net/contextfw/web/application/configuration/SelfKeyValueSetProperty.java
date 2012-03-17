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

package net.contextfw.web.application.configuration;

import java.util.Set;

import net.contextfw.web.application.internal.configuration.KeyValue;

public interface SelfKeyValueSetProperty<K, V> extends SelfAddableProperty<Set<KeyValue<K,V>>, KeyValue<K,V>> {

    SelfKeyValueSetProperty<K, V> as(K key, V value);

}