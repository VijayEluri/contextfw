package net.contextfw.web.application.internal.util;

import java.util.HashMap;
import java.util.Map;

import net.contextfw.web.application.internal.ToStringSerializer;
import net.contextfw.web.application.properties.KeyValue;
import net.contextfw.web.application.properties.Properties;
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
    public AttributeHandler(Injector injector, Properties conf) {
        
        for (KeyValue<Class<?>, Class<? extends AttributeSerializer<?>>> entry : conf
                .get(Properties.ATTRIBUTE_SERIALIZER)) {
            serializers.put(
                    entry.getKey(), 
                    (AttributeSerializer<Object>) injector.getInstance(entry.getValue()));
        }
        
        for (KeyValue<Class<?>, Class<? extends AttributeJsonSerializer<?>>> entry : conf
                .get(Properties.ATTRIBUTE_JSON_SERIALIZER)) {
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