package net.contextfw.web.application.properties;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.contextfw.web.application.PropertyProvider;
import net.contextfw.web.application.SystemPropertyProvider;
import net.contextfw.web.application.lifecycle.DefaultLifecycleListener;
import net.contextfw.web.application.lifecycle.LifecycleListener;
import net.contextfw.web.application.serialize.AttributeJsonSerializer;
import net.contextfw.web.application.serialize.AttributeSerializer;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

// 

public class Properties {
    
    private static final String KEY_NAMESPACE = "contextfw.namespace";

    private static final String KEY_ATTRIBUTE_SERIALIZER = "contextfw.attributeSerializer";

    private static final String KEY_JSON_SERIALIZER = "contextfw.jsonSerializer";

    private static final String KEY_JSON_DESERIALIZER = "contextfw.jsonDeserializer";

    private static final String KEY_ATTRIBUTE_JSON_SERIALIZER = "contextfw.attributeJsonSerializer";

    private static final String KEY_REMOVAL_SCHEDULE_PERIOD = "contextfw.removalSchedulePeriod";

   // private static final String KEY_POLL_TIME = "contextfw.pollTime";

    private static final String KEY_MAX_INACTIVITY = "contextfw.maxInactivity";

    private static final String KEY_INITIAL_MAX_INACTIVITY = "contextfw.initialMaxInactivity";

  //  private static final String KEY_ERROR_TIME = "contextfw.errorTime";

    private static final String KEY_COMPONENT_ROOT_PACKAGE = "contextfw.componentRootPackage";

    private static final String KEY_RESOURCE_PATH = "contextfw.resourcePath";

    private static final String KEY_TRANSFORMER_COUNT = "contextfw.transformerCount";

    private static final String KEY_LIFECYCLE_LISTENER = "contextfw.lifecycleListener";

    private static final String KEY_PROPERTY_PROVIDER = "contextfw.propertyProvider";

    private static final String KEY_XML_PARAM_NAME = "contextfw.xmlParamName";

    private static final String KEY_CONTEXT_PATH = "contextfw.contextPath";

    private static final String KEY_RESOURCES_PREFIX = "contextfw.resourcesPrefix";

    private static final String KEY_LOG_XML = "contextfw.logXML";

    private static final String KEY_DEBUG_MODE = "contextfw.debugMode";

    public static Properties getDefaults() {
        return new Properties()
          .set(DEBUG_MODE, true)
          .set(LOG_XML, true)
          .set(TRANSFORMER_COUNT, 1)
          .set(RESOURCES_PREFIX, "/resources")
          .set(CONTEXT_PATH, "")
          .set(XML_PARAM_NAME, null)
          .set(PROPERTY_PROVIDER, SystemPropertyProvider.class)
          .set(LIFECYCLE_LISTENER, DefaultLifecycleListener.class)
          .set(RESOURCE_PATH, new HashSet<String>())
          .set(COMPONENT_ROOT_PACKAGE, new HashSet<String>())
          // .set(ERROR_TIME.inMinsAndSecs(1, 30))
          .set(INITIAL_MAX_INACTIVITY.inSeconds(30))
          //.set(POLL_TIME.inSeconds(70))
          .set(REMOVAL_SCHEDULE_PERIOD.inMinutes(1))
          .set(MAX_INACTIVITY.inHoursAndMins(3, 0))
          .set(NAMESPACE, new HashSet<KeyValue<String, String>>())
          .set(ATTRIBUTE_JSON_SERIALIZER, new HashSet<KeyValue<Class<?>, 
                     Class<? extends AttributeJsonSerializer<?>>>>())
           .set(JSON_SERIALIZER, new HashSet<KeyValue<Class<?>, 
                     Class<? extends JsonSerializer<?>>>>())
          .set(JSON_DESERIALIZER, new HashSet<KeyValue<Class<?>, 
                     Class<? extends JsonDeserializer<?>>>>())
          .set(ATTRIBUTE_SERIALIZER, new HashSet<KeyValue<Class<?>, 
                     Class<? extends AttributeSerializer<?>>>>());
    }
    
    public static final SettableProperty<Boolean> DEBUG_MODE = 
        new BooleanProperty(KEY_DEBUG_MODE);
    
    public static final SettableProperty<Boolean> LOG_XML = 
        new BooleanProperty(KEY_LOG_XML);
    
    public static final SettableProperty<String> RESOURCES_PREFIX = 
        new StringProperty(KEY_RESOURCES_PREFIX);
    
    public static final SettableProperty<String> CONTEXT_PATH = 
        new StringProperty(KEY_CONTEXT_PATH);
    
    public static final SettableProperty<String> XML_PARAM_NAME = 
        new StringProperty(KEY_XML_PARAM_NAME);
    
    public static final SettableProperty<Class<? extends PropertyProvider>> PROPERTY_PROVIDER = 
        new ClassProperty<PropertyProvider>(KEY_PROPERTY_PROVIDER);
    
    public static final SettableProperty<Class<? extends LifecycleListener>> LIFECYCLE_LISTENER = 
        new ClassProperty<LifecycleListener>(KEY_LIFECYCLE_LISTENER);
    
    public static final SettableProperty<Integer> TRANSFORMER_COUNT =
        new RangedIntegerProperty(KEY_TRANSFORMER_COUNT, 1, 200);
    
    public static final AddableProperty<Set<String>, String> RESOURCE_PATH
        = new StringSetProperty(KEY_RESOURCE_PATH);
    
    public static final AddableProperty<Set<String>, String> COMPONENT_ROOT_PACKAGE
        = new StringSetProperty(KEY_COMPONENT_ROOT_PACKAGE);
    
//    public static final TemporalProperty ERROR_TIME = 
//        new TemporalProperty(KEY_ERROR_TIME);
    
    public static final TemporalProperty INITIAL_MAX_INACTIVITY = 
        new TemporalProperty(KEY_INITIAL_MAX_INACTIVITY);
    
    public static final TemporalProperty MAX_INACTIVITY = 
        new TemporalProperty(KEY_MAX_INACTIVITY);
    
//    public static final TemporalProperty POLL_TIME = 
//        new TemporalProperty(KEY_POLL_TIME);
    
    public static final TemporalProperty REMOVAL_SCHEDULE_PERIOD = 
        new TemporalProperty(KEY_REMOVAL_SCHEDULE_PERIOD);
    
    public static final SelfKeyValueSetProperty<String, String>
        NAMESPACE
        = new SelfKeyValueSetProperty<String, String>(KEY_NAMESPACE);
    
    public static final SelfKeyValueSetProperty<Class<?>, 
        Class<? extends AttributeJsonSerializer<?>>> ATTRIBUTE_JSON_SERIALIZER
           = new SelfKeyValueSetProperty<Class<?>, 
              Class<? extends AttributeJsonSerializer<?>>>(KEY_ATTRIBUTE_JSON_SERIALIZER 
              );
    
    public static final SelfKeyValueSetProperty<Class<?>, 
    Class<? extends JsonDeserializer<?>>> JSON_DESERIALIZER
        = new SelfKeyValueSetProperty<Class<?>, 
            Class<? extends JsonDeserializer<?>>>(KEY_JSON_DESERIALIZER);
    
    public static final SelfKeyValueSetProperty<Class<?>, 
    Class<? extends JsonSerializer<?>>> JSON_SERIALIZER
        = new SelfKeyValueSetProperty<Class<?>, 
            Class<? extends JsonSerializer<?>>>(KEY_JSON_SERIALIZER);
    
    public static final SelfKeyValueSetProperty<Class<?>, 
    Class<? extends AttributeSerializer<?>>> ATTRIBUTE_SERIALIZER
        = new SelfKeyValueSetProperty<Class<?>, 
            Class<? extends AttributeSerializer<?>>>(KEY_ATTRIBUTE_SERIALIZER);

    
    private final Map<String, Object> values;
    
    public Properties() {
        values = new HashMap<String, Object>();
    }
    
    private <T> Properties(Map<String, Object> values, Property<T> property, T value) {
        this.values = new HashMap<String, Object>();
        this.values.putAll(values);
        this.values.put(property.getKey(), property.validate(value));
    }
    
    @SuppressWarnings("unchecked")
    public <T> T get(Property<T> property) {
        return property.validate((T) values.get(property.getKey()));
    }
    
    public <T> Properties set(SettableProperty<T> property, T value) {
        return new Properties(values, property, value);
    }
    
    public <T extends Collection<V>, V> Properties set(AddableProperty<T, V> property, T value) {
        return new Properties(values, property, value);
    }
    
    public <T extends Collection<V>, V> Properties set(SelfAddableProperty<T, V> property, T value) {
        return new Properties(values, property, value);
    }
    
    public <T> Properties set(SelfSettableProperty<T> property) {
        return new Properties(values, property, property.getValue());
    }
    
    public <T extends Collection<V>, V> Properties add(AddableProperty<T, V> property, V value) {
        return new Properties(values, property, property.add(get(property), value));
    }
    
    public <T extends Collection<V>, V> Properties add(SelfAddableProperty<T, V> property) {
        return new Properties(values, property, property.add(get(property), property.getValue()));
    }
    
//    public <K, V> Properties add(KeyValueSetProperty<K, V> property) {
//        return new Properties(values, property, property.append(
//                get(property), property.getValue()));
//    }
}