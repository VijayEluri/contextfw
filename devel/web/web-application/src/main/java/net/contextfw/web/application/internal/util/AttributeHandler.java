package net.contextfw.web.application.internal.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.contextfw.web.application.ModuleConfiguration;
import net.contextfw.web.application.converter.AttributeSerializer;
import net.contextfw.web.application.converter.ObjectAttributeSerializer;
import net.contextfw.web.application.internal.ToStringSerializer;

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
    public AttributeHandler(Injector injector, ModuleConfiguration conf) {
        for (Entry<Class<?>, Class<? extends AttributeSerializer<?>>> entry : conf.getAttributeSerializerClasses()) {
            serializers.put(entry.getKey(), (AttributeSerializer<Object>) injector.getInstance(entry.getValue()));
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