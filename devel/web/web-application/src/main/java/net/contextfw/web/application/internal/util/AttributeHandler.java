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

package net.contextfw.web.application.internal.util;

import java.util.HashMap;
import java.util.Map;

import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.internal.ToStringSerializer;
import net.contextfw.web.application.internal.configuration.KeyValue;
import net.contextfw.web.application.serialize.AttributeJsonSerializer;
import net.contextfw.web.application.serialize.AttributeSerializer;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class AttributeHandler implements ObjectAttributeSerializer {
    
    private final Map<Class<?>,  AttributeSerializer<Object>> serializers = 
        new HashMap<Class<?>,  AttributeSerializer<Object>>();
    
    private ToStringSerializer toStringSerializer = new ToStringSerializer();
    
    @SuppressWarnings("unchecked")
    @Inject
    public AttributeHandler(Injector injector, Configuration conf) {
        
        for (KeyValue<Class<?>, Class<? extends AttributeSerializer<?>>> entry : conf
                .get(Configuration.ATTRIBUTE_SERIALIZER)) {
            serializers.put(
                    entry.getKey(), 
                    (AttributeSerializer<Object>) injector.getInstance(entry.getValue()));
        }
        
        for (KeyValue<Class<?>, Class<? extends AttributeJsonSerializer<?>>> entry : conf
                .get(Configuration.ATTRIBUTE_JSON_SERIALIZER)) {
            serializers.put(
                    entry.getKey(), 
                    (AttributeSerializer<Object>) injector.getInstance(entry.getValue()));
        }

    }
    
    @Override
    public String serialize(Object source) {
        if (source != null) {
            Class<?> cl = source.getClass();
            if (serializers.containsKey(cl)) {
                return serializers.get(cl).serialize(source);
            } else {
                return toStringSerializer.serialize(source);
            }
        } else {
            return null;
        }
    }
}